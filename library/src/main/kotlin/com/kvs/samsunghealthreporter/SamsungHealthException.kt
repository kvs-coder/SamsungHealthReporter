package com.kvs.samsunghealthreporter

/**
 * The single error type thrown by **SamsungHealthReporter**.
 *
 * Samsung Health Data SDK exceptions are wrapped and kept as [cause].
 */
public sealed class SamsungHealthException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /** The user has not granted the permission the call needs. */
    public class NotAuthorized(
        message: String,
        cause: Throwable? = null,
    ) : SamsungHealthException(message, cause)

    /** A type does not support the requested operation, or can't be mapped. */
    public class InvalidType(
        message: String,
        cause: Throwable? = null,
    ) : SamsungHealthException(message, cause)

    /** An input value is invalid, or a stored value can't be converted. */
    public class InvalidValue(
        message: String,
        cause: Throwable? = null,
    ) : SamsungHealthException(message, cause)

    /**
     * Samsung Health reported a problem the user can fix (install or update Samsung Health,
     * accept its policy, ...). Pass it to **SamsungHealthManager.resolve** to start the fix.
     */
    public class Resolvable internal constructor(
        message: String,
        cause: Throwable,
    ) : SamsungHealthException(message, cause)

    /** Any other Samsung Health Data SDK error. [errorCode] is the SDK error code, when known. */
    public class Platform(
        message: String,
        public val errorCode: Int? = null,
        cause: Throwable? = null,
    ) : SamsungHealthException(message, cause)
}
