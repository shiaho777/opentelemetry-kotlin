package io.opentelemetry.kotlin.propagation.utils

private val TCHAR_SPECIALS = setOf(
    '!', '#', '$', '%', '&', '\'', '*', '+', '-', '.', '^', '_', '`', '|', '~',
)

/**
 * RFC 7230 token: 1*tchar.
 * tchar = "!" / "#" / "$" / "%" / "&" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~" / DIGIT / ALPHA
 */
public fun isValidBaggageKey(name: String): Boolean {
    if (name.isEmpty()) {
        return false
    }
    return name.all(::isTChar)
}

/**
 * Reject characters that would break the W3C wire format outright (CR, LF) or are
 * meaningless inside a baggage value (NUL). Other non-octet characters are accepted
 * and percent-encoded by the propagator at inject time.
 */
public fun isValidBaggageValue(value: String): Boolean =
    value.all { c -> c != '\r' && c != '\n' && c.code != 0 }

/**
 * Metadata is copied into the baggage header after `;` and is not percent-encoded.
 * A comma would be read as another list member. Other characters outside printable
 * ASCII are not legal in that header.
 */
public fun isValidBaggageMetadata(metadata: String): Boolean =
    metadata.all { c -> c.code in PRINTABLE_ASCII && c != ',' }

private const val PRINTABLE_ASCII_MIN = 0x20
private const val PRINTABLE_ASCII_MAX = 0x7E
private val PRINTABLE_ASCII = PRINTABLE_ASCII_MIN..PRINTABLE_ASCII_MAX

private fun isTChar(c: Char): Boolean {
    if (c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9') {
        return true
    }
    return c in TCHAR_SPECIALS
}
