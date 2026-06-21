// port-lint: source samples_formats.rs
package io.github.kotlinmania.cpal

/** Conversion from one sample representation into another. */
interface FromSample<in S : Any, out T : Any> {
    fun fromSample(sample: S): T
}

/** Audio sample metadata used by CPAL's typed stream helpers. */
interface Sample<T : Any> {
    val equilibrium: T
}

/** A sample type whose CPAL wire format is known. */
interface SizedSample<T : Any> : Sample<T> {
    val format: SampleFormat

    fun cast(sample: Any): T?
}

/** Signed 24-bit sample value. */
data class I24(
    val value: Int,
) {
    init {
        require(value in MIN_VALUE..MAX_VALUE) { "I24 sample is outside the 24-bit signed range" }
    }

    companion object {
        const val MIN_VALUE: Int = -8_388_608
        const val MAX_VALUE: Int = 8_388_607
    }
}

/** Signed 48-bit sample value. */
data class I48(
    val value: Long,
) {
    init {
        require(value in MIN_VALUE..MAX_VALUE) { "I48 sample is outside the 48-bit signed range" }
    }

    companion object {
        const val MIN_VALUE: Long = -140_737_488_355_328L
        const val MAX_VALUE: Long = 140_737_488_355_327L
    }
}

/** Unsigned 24-bit sample value. */
data class U24(
    val value: UInt,
) {
    init {
        require(value <= MAX_VALUE) { "U24 sample is outside the 24-bit unsigned range" }
    }

    companion object {
        val MAX_VALUE: UInt = 16_777_215u
    }
}

/** Unsigned 48-bit sample value. */
data class U48(
    val value: ULong,
) {
    init {
        require(value <= MAX_VALUE) { "U48 sample is outside the 48-bit unsigned range" }
    }

    companion object {
        val MAX_VALUE: ULong = 281_474_976_710_655uL
    }
}

/**
 * Format that each sample has.
 *
 * This type is open to new entries in future releases.
 */
enum class SampleFormat {
    /** [Byte] with a valid range of [Byte.MIN_VALUE] through [Byte.MAX_VALUE], with zero as origin. */
    I8,

    /** [Short] with a valid range of [Short.MIN_VALUE] through [Short.MAX_VALUE], with zero as origin. */
    I16,

    /** [Int] with a valid range of [Int.MIN_VALUE] through [Int.MAX_VALUE], with zero as origin. */
    I32,

    /** [Long] with a valid range of [Long.MIN_VALUE] through [Long.MAX_VALUE], with zero as origin. */
    I64,

    /** [UByte] with a valid range of [UByte.MIN_VALUE] through [UByte.MAX_VALUE], with 128 as origin. */
    U8,

    /** [UShort] with a valid range of [UShort.MIN_VALUE] through [UShort.MAX_VALUE], with 32768 as origin. */
    U16,

    /** [UInt] with a valid range of [UInt.MIN_VALUE] through [UInt.MAX_VALUE], with 2147483648 as origin. */
    U32,

    /** [ULong] with a valid range of [ULong.MIN_VALUE] through [ULong.MAX_VALUE], with 9223372036854775808 as origin. */
    U64,

    /** [Float] with a valid range of -1.0 through 1.0, with zero as origin. */
    F32,

    /** [Double] with a valid range of -1.0 through 1.0, with zero as origin. */
    F64,

    ;

    /** Returns the size in bytes of a sample of this format. */
    fun sampleSize(): Int =
        when (this) {
            I8, U8 -> 1
            I16, U16 -> 2
            I32, U32, F32 -> 4
            I64, U64, F64 -> 8
        }

    fun isInt(): Boolean =
        when (this) {
            I8, I16, I32, I64 -> true
            U8, U16, U32, U64, F32, F64 -> false
        }

    fun isUInt(): Boolean =
        when (this) {
            U8, U16, U32, U64 -> true
            I8, I16, I32, I64, F32, F64 -> false
        }

    fun isFloat(): Boolean =
        when (this) {
            F32, F64 -> true
            I8, I16, I32, I64, U8, U16, U32, U64 -> false
        }

    override fun toString(): String =
        when (this) {
            I8 -> "i8"
            I16 -> "i16"
            I32 -> "i32"
            I64 -> "i64"
            U8 -> "u8"
            U16 -> "u16"
            U32 -> "u32"
            U64 -> "u64"
            F32 -> "f32"
            F64 -> "f64"
        }
}

object I8Sample : SizedSample<Byte> {
    override val equilibrium: Byte = 0
    override val format: SampleFormat = SampleFormat.I8

    override fun cast(sample: Any): Byte? = sample as? Byte
}

object I16Sample : SizedSample<Short> {
    override val equilibrium: Short = 0
    override val format: SampleFormat = SampleFormat.I16

    override fun cast(sample: Any): Short? = sample as? Short
}

object I32Sample : SizedSample<Int> {
    override val equilibrium: Int = 0
    override val format: SampleFormat = SampleFormat.I32

    override fun cast(sample: Any): Int? = sample as? Int
}

object I64Sample : SizedSample<Long> {
    override val equilibrium: Long = 0L
    override val format: SampleFormat = SampleFormat.I64

    override fun cast(sample: Any): Long? = sample as? Long
}

object U8Sample : SizedSample<UByte> {
    override val equilibrium: UByte = 128u
    override val format: SampleFormat = SampleFormat.U8

    override fun cast(sample: Any): UByte? = sample as? UByte
}

object U16Sample : SizedSample<UShort> {
    override val equilibrium: UShort = 32_768u
    override val format: SampleFormat = SampleFormat.U16

    override fun cast(sample: Any): UShort? = sample as? UShort
}

object U32Sample : SizedSample<UInt> {
    override val equilibrium: UInt = 2_147_483_648u
    override val format: SampleFormat = SampleFormat.U32

    override fun cast(sample: Any): UInt? = sample as? UInt
}

object U64Sample : SizedSample<ULong> {
    override val equilibrium: ULong = 9_223_372_036_854_775_808uL
    override val format: SampleFormat = SampleFormat.U64

    override fun cast(sample: Any): ULong? = sample as? ULong
}

object F32Sample : SizedSample<Float> {
    override val equilibrium: Float = 0.0f
    override val format: SampleFormat = SampleFormat.F32

    override fun cast(sample: Any): Float? = sample as? Float
}

object F64Sample : SizedSample<Double> {
    override val equilibrium: Double = 0.0
    override val format: SampleFormat = SampleFormat.F64

    override fun cast(sample: Any): Double? = sample as? Double
}
