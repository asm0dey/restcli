package uos.dev.restcli.parser

data class Yytoken @JvmOverloads constructor(
    val type: TokenType,
    val value: String = ""
) {
    override fun toString(): String = "${type.name}($value)"
}

enum class TokenType {
    TYPE_VALUE_FILE_REF,
    TYPE_REQUEST_METHOD,
    TYPE_REQUEST_TARGET,
    TYPE_REQUEST_HTTP_VERSION,
    TYPE_FIELD_NAME,
    TYPE_FIELD_VALUE,
    TYPE_BODY_MESSAGE,
    TYPE_SEPARATOR,
    TYPE_BLANK,
    TYPE_HANDLER_FILE_SCRIPT,
    TYPE_HANDLER_EMBEDDED_SCRIPT,
    TYPE_RESPONSE_REFERENCE,
    TYPE_COMMENT;
}
