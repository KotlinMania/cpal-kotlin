// port-lint: source lib.rs
package io.github.kotlinmania.cpal

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/** A host's device iterator yielding only input devices. */
typealias InputDevices<D> = Sequence<D>

/** A host's device iterator yielding only output devices. */
typealias OutputDevices<D> = Sequence<D>

/** Number of channels. */
typealias ChannelCount = UShort

/** The number of samples processed per second for a single channel of audio. */
data class SampleRate(
    val value: UInt,
) : Comparable<SampleRate> {
    operator fun times(rhs: UInt): SampleRate = SampleRate(value * rhs)

    operator fun div(rhs: UInt): SampleRate = SampleRate(value / rhs)

    override fun compareTo(other: SampleRate): Int = value.compareTo(other.value)
}

/** The desired number of frames for the hardware buffer. */
typealias FrameCount = UInt

/** The buffer size used by the device. */
sealed class BufferSize {
    /**
     * Used when no specific buffer size is set and uses the default behavior of the given host.
     * The default buffer size may be surprisingly large, leading to latency issues.
     */
    data object Default : BufferSize()

    /**
     * Used when low latency is desired, in accordance with the [SupportedBufferSize] range
     * produced by the [SupportedStreamConfig] API.
     */
    data class Fixed(
        val frameCount: FrameCount,
    ) : BufferSize()
}

/**
 * The set of parameters used to describe how to open a stream.
 *
 * The sample format is omitted in favour of using a sample type.
 */
data class StreamConfig(
    val channels: ChannelCount,
    val sampleRate: SampleRate,
    val bufferSize: BufferSize,
)

/** Describes the minimum and maximum supported buffer size for the device. */
sealed class SupportedBufferSize {
    data class Range(
        val min: FrameCount,
        val max: FrameCount,
    ) : SupportedBufferSize()

    /** In the case that the platform provides no way of getting the default buffersize before starting a stream. */
    data object Unknown : SupportedBufferSize()
}

/**
 * Describes a range of supported stream configurations, retrieved via the
 * [DeviceTrait.supportedInputConfigs] and [DeviceTrait.supportedOutputConfigs] methods.
 */
data class SupportedStreamConfigRange(
    private val channels: ChannelCount,
    private val minSampleRate: SampleRate,
    private val maxSampleRate: SampleRate,
    private val bufferSize: SupportedBufferSize,
    private val sampleFormat: SampleFormat,
) {
    fun channels(): ChannelCount = channels

    fun minSampleRate(): SampleRate = minSampleRate

    fun maxSampleRate(): SampleRate = maxSampleRate

    fun bufferSize(): SupportedBufferSize = bufferSize

    fun sampleFormat(): SampleFormat = sampleFormat

    /**
     * Retrieve a [SupportedStreamConfig] with the given sample rate and buffer size.
     *
     * Throws if the given [sampleRate] is outside the range specified within this
     * [SupportedStreamConfigRange] instance. For a non-throwing variant, use
     * [tryWithSampleRate].
     */
    fun withSampleRate(sampleRate: SampleRate): SupportedStreamConfig =
        tryWithSampleRate(sampleRate) ?: throw IllegalArgumentException("sample rate out of range")

    /**
     * Retrieve a [SupportedStreamConfig] with the given sample rate and buffer size.
     *
     * Returns `null` if the given sample rate is outside the range specified within this
     * [SupportedStreamConfigRange] instance.
     */
    fun tryWithSampleRate(sampleRate: SampleRate): SupportedStreamConfig? =
        if (minSampleRate <= sampleRate && sampleRate <= maxSampleRate) {
            SupportedStreamConfig(channels, sampleRate, bufferSize, sampleFormat)
        } else {
            null
        }

    /** Turns this range into a [SupportedStreamConfig] corresponding to the maximum samples rate. */
    fun withMaxSampleRate(): SupportedStreamConfig =
        SupportedStreamConfig(channels, maxSampleRate, bufferSize, sampleFormat)

    /**
     * A comparison function which compares two [SupportedStreamConfigRange]s in terms of their
     * priority of use as a default stream format.
     *
     * Some backends do not provide a default stream format for their audio devices. In these cases,
     * CPAL attempts to decide on a reasonable default format for the user. To do this we use the
     * greatest of all supported stream formats when compared with this method.
     *
     * Supported stream configs are prioritised by channels, then sample format, then sample rate.
     */
    fun cmpDefaultHeuristics(other: SupportedStreamConfigRange): Int {
        val cmpStereo = compareBooleans(channels == 2.toUShort(), other.channels == 2.toUShort())
        if (cmpStereo != 0) {
            return cmpStereo
        }

        val cmpMono = compareBooleans(channels == 1.toUShort(), other.channels == 1.toUShort())
        if (cmpMono != 0) {
            return cmpMono
        }

        val cmpChannels = channels.compareTo(other.channels)
        if (cmpChannels != 0) {
            return cmpChannels
        }

        val cmpF32 = compareBooleans(sampleFormat == SampleFormat.F32, other.sampleFormat == SampleFormat.F32)
        if (cmpF32 != 0) {
            return cmpF32
        }

        val cmpI16 = compareBooleans(sampleFormat == SampleFormat.I16, other.sampleFormat == SampleFormat.I16)
        if (cmpI16 != 0) {
            return cmpI16
        }

        val cmpU16 = compareBooleans(sampleFormat == SampleFormat.U16, other.sampleFormat == SampleFormat.U16)
        if (cmpU16 != 0) {
            return cmpU16
        }

        val hz44100 = SampleRate(44_100u)
        val r44100InThis = minSampleRate <= hz44100 && hz44100 <= maxSampleRate
        val r44100InOther = other.minSampleRate <= hz44100 && hz44100 <= other.maxSampleRate
        val cmpR44100 = compareBooleans(r44100InThis, r44100InOther)
        if (cmpR44100 != 0) {
            return cmpR44100
        }

        return maxSampleRate.compareTo(other.maxSampleRate)
    }
}

/**
 * Describes a single supported stream configuration, retrieved via either a
 * [SupportedStreamConfigRange] instance or one of the default config methods on [DeviceTrait].
 */
data class SupportedStreamConfig(
    private val channels: ChannelCount,
    private val sampleRate: SampleRate,
    private val bufferSize: SupportedBufferSize,
    private val sampleFormat: SampleFormat,
) {
    fun channels(): ChannelCount = channels

    fun sampleRate(): SampleRate = sampleRate

    fun bufferSize(): SupportedBufferSize = bufferSize

    fun sampleFormat(): SampleFormat = sampleFormat

    fun config(): StreamConfig =
        StreamConfig(
            channels = channels,
            sampleRate = sampleRate,
            bufferSize = BufferSize.Default,
        )
}

/** Converts this supported stream configuration into a stream configuration. */
fun SupportedStreamConfig.toStreamConfig(): StreamConfig = config()

/**
 * A buffer of dynamically typed audio data, passed to raw stream callbacks.
 *
 * Raw input stream callbacks receive [Data], while raw output stream callbacks expect mutable
 * access to [Data].
 */
class Data internal constructor(
    private val samples: MutableList<Any>,
    private val sampleFormat: SampleFormat,
) {
    /** The sample format of the internal audio data. */
    fun sampleFormat(): SampleFormat = sampleFormat

    /** The full length of the buffer in samples. */
    fun len(): Int = samples.size

    /** The raw memory representation of the underlying audio data as bytes. */
    fun bytes(): ByteArray {
        val out = ByteArray(samples.size * sampleFormat.sampleSize())
        var offset = 0
        for (sample in samples) {
            offset = sample.writeSampleBytes(sampleFormat, out, offset)
        }
        return out
    }

    /** Access the data as a list of sample type [T]. */
    fun <T : Any> asList(sample: SizedSample<T>): List<T>? {
        if (sample.format != sampleFormat) {
            return null
        }
        val out = ArrayList<T>(samples.size)
        for (value in samples) {
            out += sample.cast(value) ?: return null
        }
        return out
    }

    /** Access the data as a mutable list of sample type [T]. */
    fun <T : Any> asMutableList(sample: SizedSample<T>): MutableList<T>? {
        if (sample.format != sampleFormat) {
            return null
        }
        return object : AbstractMutableList<T>() {
            override val size: Int
                get() = samples.size

            override fun get(index: Int): T =
                sample.cast(samples[index]) ?: throw IllegalStateException("host supplied incorrect sample type")

            override fun set(index: Int, element: T): T {
                val previous = get(index)
                samples[index] = element
                return previous
            }

            override fun add(index: Int, element: T): Unit = throw UnsupportedOperationException("audio callback buffers have a fixed length")

            override fun removeAt(index: Int): T = throw UnsupportedOperationException("audio callback buffers have a fixed length")
        }
    }
}

private fun compareBooleans(left: Boolean, right: Boolean): Int =
    when {
        left == right -> 0
        left -> 1
        else -> -1
    }

private fun Any.writeSampleBytes(format: SampleFormat, out: ByteArray, offset: Int): Int =
    when (format) {
        SampleFormat.I8 -> writeLong((this as Byte).toLong(), out, offset, 1)
        SampleFormat.I16 -> writeLong((this as Short).toLong(), out, offset, 2)
        SampleFormat.I32 -> writeLong((this as Int).toLong(), out, offset, 4)
        SampleFormat.I64 -> writeLong(this as Long, out, offset, 8)
        SampleFormat.U8 -> writeULong((this as UByte).toULong(), out, offset, 1)
        SampleFormat.U16 -> writeULong((this as UShort).toULong(), out, offset, 2)
        SampleFormat.U32 -> writeULong((this as UInt).toULong(), out, offset, 4)
        SampleFormat.U64 -> writeULong(this as ULong, out, offset, 8)
        SampleFormat.F32 -> writeLong((this as Float).toRawBits().toLong(), out, offset, 4)
        SampleFormat.F64 -> writeLong((this as Double).toRawBits(), out, offset, 8)
    }

private fun writeLong(value: Long, out: ByteArray, offset: Int, byteCount: Int): Int {
    var current = value
    for (i in 0 until byteCount) {
        out[offset + i] = (current and 0xff).toByte()
        current = current shr 8
    }
    return offset + byteCount
}

private fun writeULong(value: ULong, out: ByteArray, offset: Int, byteCount: Int): Int {
    var current = value
    for (i in 0 until byteCount) {
        out[offset + i] = (current and 0xffu).toByte()
        current = current shr 8
    }
    return offset + byteCount
}

/** A monotonic time instance associated with a stream. */
class StreamInstant internal constructor(
    private val secs: Long,
    private val nanos: UInt,
) : Comparable<StreamInstant> {
    init {
        require(nanos < 1_000_000_000u) { "subsecond nanoseconds must be less than one second" }
    }

    /**
     * The amount of time elapsed from another instant to this one.
     *
     * Returns `null` if [earlier] is later than this instant.
     */
    fun durationSince(earlier: StreamInstant): Duration? {
        if (this < earlier) {
            return null
        }
        val seconds = secs - earlier.secs
        val nanosDelta = nanos.toLong() - earlier.nanos.toLong()
        return if (nanosDelta >= 0) {
            seconds.seconds + nanosDelta.nanoseconds
        } else {
            (seconds - 1).seconds + (1_000_000_000L + nanosDelta).nanoseconds
        }
    }

    /**
     * Returns the instant in time after the given duration has passed.
     *
     * Returns `null` if the resulting instant would exceed the bounds of the underlying data
     * structure.
     */
    fun add(duration: Duration): StreamInstant? {
        val wholeSeconds = duration.inWholeSeconds
        val subsecondNanos = (duration - wholeSeconds.seconds).inWholeNanoseconds
        return addParts(wholeSeconds, subsecondNanos)
    }

    /**
     * Returns the instant in time one [duration] ago.
     *
     * Returns `null` if the resulting instant would underflow. As a result, it is important to
     * consider that on some platforms the [StreamInstant] may begin at zero from the moment the
     * source stream is created.
     */
    fun sub(duration: Duration): StreamInstant? {
        val wholeSeconds = duration.inWholeSeconds
        val subsecondNanos = (duration - wholeSeconds.seconds).inWholeNanoseconds
        return addParts(-wholeSeconds, -subsecondNanos)
    }

    override fun compareTo(other: StreamInstant): Int {
        val secCmp = secs.compareTo(other.secs)
        return if (secCmp != 0) secCmp else nanos.compareTo(other.nanos)
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is StreamInstant && secs == other.secs && nanos == other.nanos)

    override fun hashCode(): Int = 31 * secs.hashCode() + nanos.hashCode()

    override fun toString(): String = "StreamInstant(secs=$secs, nanos=$nanos)"

    private fun addParts(seconds: Long, subsecondNanos: Long): StreamInstant? {
        var nextSecs = checkedAdd(secs, seconds) ?: return null
        var nextNanos = nanos.toLong() + subsecondNanos
        if (nextNanos >= 1_000_000_000L) {
            nextSecs = checkedAdd(nextSecs, 1) ?: return null
            nextNanos -= 1_000_000_000L
        } else if (nextNanos < 0) {
            nextSecs = checkedAdd(nextSecs, -1) ?: return null
            nextNanos += 1_000_000_000L
        }
        return StreamInstant(nextSecs, nextNanos.toUInt())
    }

    companion object {
        fun new(secs: Long, nanos: UInt): StreamInstant = StreamInstant(secs, nanos)
    }
}

private fun checkedAdd(lhs: Long, rhs: Long): Long? {
    val result = lhs + rhs
    return if (((lhs xor result) and (rhs xor result)) < 0) null else result
}

/** A timestamp associated with a call to an input stream's data callback. */
data class InputStreamTimestamp(
    /** The instant the stream's data callback was invoked. */
    val callback: StreamInstant,
    /** The instant that data was captured from the device. */
    val capture: StreamInstant,
)

/** A timestamp associated with a call to an output stream's data callback. */
data class OutputStreamTimestamp(
    /** The instant the stream's data callback was invoked. */
    val callback: StreamInstant,
    /** The predicted instant that data written will be delivered to the device for playback. */
    val playback: StreamInstant,
)

/** Information relevant to a single call to the user's input stream data callback. */
data class InputCallbackInfo(
    private val timestamp: InputStreamTimestamp,
) {
    /** The timestamp associated with the call to an input stream's data callback. */
    fun timestamp(): InputStreamTimestamp = timestamp
}

/** Information relevant to a single call to the user's output stream data callback. */
data class OutputCallbackInfo(
    private val timestamp: OutputStreamTimestamp,
) {
    /** The timestamp associated with the call to an output stream's data callback. */
    fun timestamp(): OutputStreamTimestamp = timestamp
}
