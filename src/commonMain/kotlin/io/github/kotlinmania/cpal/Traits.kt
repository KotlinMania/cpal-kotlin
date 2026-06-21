// port-lint: source traits.rs
package io.github.kotlinmania.cpal

import kotlin.time.Duration

/** A [Host] provides access to the available audio devices on the system. */
interface HostTrait<D : DeviceTrait<*>> {
    /** Whether or not the host is available on the system. */
    fun isAvailable(): Boolean

    /**
     * A sequence yielding all [DeviceTrait]s currently available to the host on the system.
     *
     * Can be empty if the system does not support audio in general.
     */
    fun devices(): Result<Sequence<D>>

    /**
     * The default input audio device on the system.
     *
     * Returns `null` if no input device is available.
     */
    fun defaultInputDevice(): D?

    /**
     * The default output audio device on the system.
     *
     * Returns `null` if no output device is available.
     */
    fun defaultOutputDevice(): D?

    /**
     * A sequence yielding all devices currently available to the system that support one or more
     * input stream formats.
     *
     * Can be empty if the system does not support audio input.
     */
    fun inputDevices(): Result<InputDevices<D>> =
        devices().mapCatching { devices ->
            devices.filter { device ->
                device
                    .supportedInputConfigs()
                    .getOrNull()
                    ?.iterator()
                    ?.hasNext() ?: false
            }
        }

    /**
     * A sequence yielding all devices currently available to the system that support one or more
     * output stream formats.
     *
     * Can be empty if the system does not support audio output.
     */
    fun outputDevices(): Result<OutputDevices<D>> =
        devices().mapCatching { devices ->
            devices.filter { device ->
                device
                    .supportedOutputConfigs()
                    .getOrNull()
                    ?.iterator()
                    ?.hasNext() ?: false
            }
        }
}

/** A device that is capable of audio input and/or output. */
interface DeviceTrait<S : StreamTrait> {
    /** The human-readable name of the device. */
    fun name(): Result<String>

    /**
     * A sequence yielding formats that are supported by the backend.
     *
     * Can return an error if the device is no longer valid, for example if it has been
     * disconnected.
     */
    fun supportedInputConfigs(): Result<Sequence<SupportedStreamConfigRange>>

    /**
     * A sequence yielding output stream formats that are supported by the device.
     *
     * Can return an error if the device is no longer valid, for example if it has been
     * disconnected.
     */
    fun supportedOutputConfigs(): Result<Sequence<SupportedStreamConfigRange>>

    /** The default input stream format for the device. */
    fun defaultInputConfig(): Result<SupportedStreamConfig>

    /** The default output stream format for the device. */
    fun defaultOutputConfig(): Result<SupportedStreamConfig>

    /** Create an input stream. */
    fun <T : Any> buildInputStream(
        config: StreamConfig,
        sample: SizedSample<T>,
        dataCallback: (List<T>, InputCallbackInfo) -> Unit,
        errorCallback: (StreamError) -> Unit,
        timeout: Duration?,
    ): Result<S> =
        buildInputStreamRaw(
            config = config,
            sampleFormat = sample.format,
            dataCallback = { data, info ->
                dataCallback(
                    data.asList(sample)
                        ?: throw IllegalStateException("host supplied incorrect sample type"),
                    info,
                )
            },
            errorCallback = errorCallback,
            timeout = timeout,
        )

    /** Create an output stream. */
    fun <T : Any> buildOutputStream(
        config: StreamConfig,
        sample: SizedSample<T>,
        dataCallback: (MutableList<T>, OutputCallbackInfo) -> Unit,
        errorCallback: (StreamError) -> Unit,
        timeout: Duration?,
    ): Result<S> =
        buildOutputStreamRaw(
            config = config,
            sampleFormat = sample.format,
            dataCallback = { data, info ->
                dataCallback(
                    data.asMutableList(sample)
                        ?: throw IllegalStateException("host supplied incorrect sample type"),
                    info,
                )
            },
            errorCallback = errorCallback,
            timeout = timeout,
        )

    /** Create a dynamically typed input stream. */
    fun buildInputStreamRaw(
        config: StreamConfig,
        sampleFormat: SampleFormat,
        dataCallback: (Data, InputCallbackInfo) -> Unit,
        errorCallback: (StreamError) -> Unit,
        timeout: Duration?,
    ): Result<S>

    /** Create a dynamically typed output stream. */
    fun buildOutputStreamRaw(
        config: StreamConfig,
        sampleFormat: SampleFormat,
        dataCallback: (Data, OutputCallbackInfo) -> Unit,
        errorCallback: (StreamError) -> Unit,
        timeout: Duration?,
    ): Result<S>
}

/** A stream created from [DeviceTrait], with methods to control playback. */
interface StreamTrait {
    /**
     * Run the stream.
     *
     * Note: Not all platforms automatically run the stream upon creation, so it is important to
     * call [play] after creation if it is expected that the stream should run immediately.
     */
    fun play(): Result<Unit>

    /**
     * Some devices support pausing the audio stream. This can be useful for saving energy in
     * moments of silence.
     *
     * Note: Not all devices support suspending the stream at the hardware level. This method may
     * fail in these cases.
     */
    fun pause(): Result<Unit>
}

/** Public host type supplied by platform backends once those modules are ported. */
typealias Host = HostTrait<*>

/** Public device type supplied by platform backends once those modules are ported. */
typealias Device = DeviceTrait<*>

/** Public stream type supplied by platform backends once those modules are ported. */
typealias Stream = StreamTrait
