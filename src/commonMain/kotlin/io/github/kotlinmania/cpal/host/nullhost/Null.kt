// port-lint: source host/null/mod.rs
package io.github.kotlinmania.cpal.host.nullhost

import io.github.kotlinmania.cpal.Data
import io.github.kotlinmania.cpal.DeviceTrait
import io.github.kotlinmania.cpal.HostTrait
import io.github.kotlinmania.cpal.InputCallbackInfo
import io.github.kotlinmania.cpal.OutputCallbackInfo
import io.github.kotlinmania.cpal.SampleFormat
import io.github.kotlinmania.cpal.StreamConfig
import io.github.kotlinmania.cpal.StreamError
import io.github.kotlinmania.cpal.StreamTrait
import io.github.kotlinmania.cpal.SupportedStreamConfig
import io.github.kotlinmania.cpal.SupportedStreamConfigRange
import kotlin.time.Duration

public class NullDevices : Sequence<NullDevice> {
    override fun iterator(): Iterator<NullDevice> = emptySequence<NullDevice>().iterator()

    public companion object {
        public fun new(): Result<NullDevices> = Result.success(NullDevices())
    }
}

public class NullDevice : DeviceTrait<NullStream> {
    override fun name(): Result<String> = Result.success("null")

    override fun supportedInputConfigs(): Result<Sequence<SupportedStreamConfigRange>> =
        Result.success(emptySequence())

    override fun supportedOutputConfigs(): Result<Sequence<SupportedStreamConfigRange>> =
        Result.success(emptySequence())

    override fun defaultInputConfig(): Result<SupportedStreamConfig> =
        Result.failure(UnsupportedOperationException("null device has no default input config"))

    override fun defaultOutputConfig(): Result<SupportedStreamConfig> =
        Result.failure(UnsupportedOperationException("null device has no default output config"))

    override fun buildInputStreamRaw(
        config: StreamConfig,
        sampleFormat: SampleFormat,
        dataCallback: (Data, InputCallbackInfo) -> Unit,
        errorCallback: (StreamError) -> Unit,
        timeout: Duration?,
    ): Result<NullStream> =
        Result.failure(UnsupportedOperationException("null device cannot build input stream"))

    override fun buildOutputStreamRaw(
        config: StreamConfig,
        sampleFormat: SampleFormat,
        dataCallback: (Data, OutputCallbackInfo) -> Unit,
        errorCallback: (StreamError) -> Unit,
        timeout: Duration?,
    ): Result<NullStream> =
        Result.failure(UnsupportedOperationException("null device cannot build output stream"))

    override fun equals(other: Any?): Boolean = other is NullDevice

    override fun hashCode(): Int = "NullDevice".hashCode()

    override fun toString(): String = "NullDevice"
}

public class NullHost : HostTrait<NullDevice> {
    override fun isAvailable(): Boolean = false

    override fun devices(): Result<Sequence<NullDevice>> = Result.success(emptySequence())

    override fun defaultInputDevice(): NullDevice? = null

    override fun defaultOutputDevice(): NullDevice? = null

    public companion object {
        public fun new(): Result<NullHost> = Result.success(NullHost())
    }
}

public class NullStream : StreamTrait {
    override fun play(): Result<Unit> =
        Result.failure(UnsupportedOperationException("null stream cannot play"))

    override fun pause(): Result<Unit> =
        Result.failure(UnsupportedOperationException("null stream cannot pause"))

    override fun equals(other: Any?): Boolean = other is NullStream

    override fun hashCode(): Int = "NullStream".hashCode()

    override fun toString(): String = "NullStream"
}
