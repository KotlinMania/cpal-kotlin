// port-lint: source error.rs
package io.github.kotlinmania.cpal

/** The requested host, although supported on this platform, is unavailable. */
data object HostUnavailable : RuntimeException("the requested host is unavailable") {
    override fun toString(): String = message.orEmpty()
}

/**
 * Some error has occurred that is specific to the backend from which it was produced.
 *
 * This error is often used as a catch-all in cases where:
 *
 * - It is unclear exactly what error might be produced by the backend API.
 * - It does not make sense to add a variant to the enclosing error type.
 * - No error was expected to occur at all, but an error is returned to avoid an unforeseen or
 *   unknown failure.
 *
 * **Note:** If you notice a [BackendSpecificError] that you believe could be better handled in a
 * cross-platform manner, please create an issue or submit a pull request with a patch that adds
 * the necessary error variant to the appropriate error type.
 */
class BackendSpecificError(val description: String) :
    RuntimeException("A backend-specific error has occurred: $description") {
    override fun toString(): String = message.orEmpty()
}

private fun backendSpecificMessage(err: BackendSpecificError): String = err.toString()

/** An error that might occur while attempting to enumerate the available devices on a system. */
sealed class DevicesError(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        DevicesError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/** An error that may occur while attempting to retrieve a device name. */
sealed class DeviceNameError(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        DeviceNameError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/** Error that can happen when enumerating the list of supported formats. */
sealed class SupportedStreamConfigsError(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause) {
    /**
     * The device no longer exists. This can happen if the device is disconnected while the program
     * is running.
     */
    data object DeviceNotAvailable : SupportedStreamConfigsError(
        "The requested device is no longer available. For example, it has been unplugged.",
    )

    /** We called something the C layer did not understand. */
    data object InvalidArgument : SupportedStreamConfigsError(
        "Invalid argument passed to the backend. For example, this happens when trying to read capture capabilities when the device does not support it.",
    )

    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        SupportedStreamConfigsError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/** May occur when attempting to request the default input or output stream format from a [Device]. */
sealed class DefaultStreamConfigError(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause) {
    /**
     * The device no longer exists. This can happen if the device is disconnected while the program
     * is running.
     */
    data object DeviceNotAvailable : DefaultStreamConfigError(
        "The requested device is no longer available. For example, it has been unplugged.",
    )

    /** Returned if, for example, the default input format was requested on an output-only device. */
    data object StreamTypeNotSupported : DefaultStreamConfigError(
        "The requested stream type is not supported by the device.",
    )

    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        DefaultStreamConfigError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/** Error that can happen when creating a [Stream]. */
sealed class BuildStreamError(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /**
     * The device no longer exists. This can happen if the device is disconnected while the program
     * is running.
     */
    data object DeviceNotAvailable : BuildStreamError(
        "The requested device is no longer available. For example, it has been unplugged.",
    )

    /** The specified stream configuration is not supported. */
    data object StreamConfigNotSupported : BuildStreamError(
        "The requested stream configuration is not supported by the device.",
    )

    /**
     * We called something the C layer did not understand.
     *
     * On ALSA, device functions called with a feature they do not support will yield this. For
     * example, trying to use capture capabilities on an output-only format yields this.
     */
    data object InvalidArgument : BuildStreamError(
        "The requested device does not support this capability (invalid argument)",
    )

    /** Occurs if adding a new stream ID would cause an integer overflow. */
    data object StreamIdOverflow : BuildStreamError("Adding a new stream ID would cause an overflow")

    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        BuildStreamError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/**
 * Errors that might occur when calling [StreamTrait.play].
 *
 * As of writing this, only macOS may immediately return an error while calling this method. This
 * is because both the ALSA and WASAPI backends only enqueue these commands and do not process them
 * immediately.
 */
sealed class PlayStreamError(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /** The device associated with the stream is no longer available. */
    data object DeviceNotAvailable :
        PlayStreamError("the device associated with the stream is no longer available")

    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        PlayStreamError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/**
 * Errors that might occur when calling [StreamTrait.pause].
 *
 * As of writing this, only macOS may immediately return an error while calling this method. This
 * is because both the ALSA and WASAPI backends only enqueue these commands and do not process them
 * immediately.
 */
sealed class PauseStreamError(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /** The device associated with the stream is no longer available. */
    data object DeviceNotAvailable :
        PauseStreamError("the device associated with the stream is no longer available")

    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        PauseStreamError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}

/** Errors that might occur while a stream is running. */
sealed class StreamError(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /**
     * The device no longer exists. This can happen if the device is disconnected while the program
     * is running.
     */
    data object DeviceNotAvailable : StreamError(
        "The requested device is no longer available. For example, it has been unplugged.",
    )

    /** See the [BackendSpecificError] docs for more information about this error variant. */
    data class BackendSpecific(val err: BackendSpecificError) :
        StreamError(backendSpecificMessage(err), err)

    override fun toString(): String = message.orEmpty()
}
