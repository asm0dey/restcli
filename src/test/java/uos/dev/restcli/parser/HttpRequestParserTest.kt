package uos.dev.restcli.parser

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import uos.dev.restcli.TestResourceLoader

class HttpRequestParserTest {
    private val parser = HttpRequestParser()

    @Test
    fun parse_get() {
        val reader = TestResourceLoader.testResourceReader(TEST_GET_REQUESTS_RESOURCE)
        val result = parser.parse(reader)
        assertThat(result.first()).isEqualTo(
            Request(
                method = RequestMethod.GET,
                requestTarget = "https://httpbin.org/ip",
                headers = mapOf("Accept" to "application/json"),
                httpVersion = Request.DEFAULT_HTTP_VERSION
            )
        )

        assertThat(result.last()).isEqualTo(
            Request(
                method = RequestMethod.GET,
                requestTarget = "http://httpbin.org/anything?id={{\$uuid}}&ts={{\$timestamp}}",
                httpVersion = Request.DEFAULT_HTTP_VERSION,
                headers = emptyMap()
            )
        )
    }

    @Test
    fun parse_post() {
        val reader = TestResourceLoader.testResourceReader(TEST_POST_REQUESTS_RESOURCE)
        val result = parser.parse(reader)
        assertThat(result.size).isEqualTo(4)
        assertThat(result.first()).isEqualTo(
            Request(
                method = RequestMethod.POST,
                requestTarget = "https://httpbin.org/post",
                headers = mapOf("Content-Type" to "application/json"),
                httpVersion = Request.DEFAULT_HTTP_VERSION,
                body = "{\n" +
                        "  \"id\": 999,\n" +
                        "  \"value\": \"content\"\n" +
                        "}"
            )
        )

        assertThat(result[2]).isEqualTo(
            Request(
                method = RequestMethod.POST,
                requestTarget = "https://httpbin.org/post",
                headers = mapOf("Content-Type" to "multipart/form-data; boundary=WebAppBoundary"),
                httpVersion = Request.DEFAULT_HTTP_VERSION,
                body = null,
                parts = listOf(
                    Request.Part(
                        name = "element-name",
                        headers = mapOf(
                            "Content-Disposition" to "form-data; name=\"element-name\"",
                            "Content-Type" to "text/plain"
                        ),
                        body = "Name"
                    ),
                    Request.Part(
                        name = "data",
                        headers = mapOf(
                            "Content-Disposition" to "form-data; name=\"data\"; filename=\"data.json\"",
                            "Content-Type" to "application/json"
                        ),
                        body = "< ./request-form-data.json"
                    )
                )
            )
        )
    }

    @Test
    fun parse_request_with_tests() {
        val reader = TestResourceLoader.testResourceReader(TEST_REQUESTS_WITH_TESTS_RESOURCE)
        val result = parser.parse(reader)
        assertThat(result.size).isEqualTo(4)

        assertThat(result.first()).isEqualTo(
            Request(
                method = RequestMethod.GET,
                requestTarget = "https://httpbin.org/status/200",
                headers = emptyMap(),
                httpVersion = Request.DEFAULT_HTTP_VERSION,
                body = null,
                scriptHandler = "> {%\n" +
                        "client.test(\"Request executed successfully\", function() {\n" +
                        "  client.assert(response.status === 200, \"Response status is not 200\");\n" +
                        "});\n" +
                        "%}"
            )
        )

        assertThat(result[2]).isEqualTo(
            Request(
                method = RequestMethod.GET,
                requestTarget = "https://httpbin.org/get",
                headers = emptyMap(),
                httpVersion = Request.DEFAULT_HTTP_VERSION,
                body = null,
                scriptHandler = "> {%\n" +
                        "client.test(\"Request executed successfully\", function() {\n" +
                        "  client.assert(response.status === 200, \"Response status is not 200\");\n" +
                        "});\n" +
                        "\n" +
                        "client.test(\"Response content-type is json\", function() {\n" +
                        "  var type = response.contentType.mimeType;\n" +
                        "  client.assert(type === \"application/json\", \"Expected 'application/json' but received '\" + type + \"'\");\n" +
                        "});\n" +
                        "%}"
            )
        )
    }

    @Test
    fun debug() {
        val path = TestResourceLoader.testResourcePath(TEST_POST_REQUESTS_RESOURCE)
        Yylex.main(arrayOf(path))
    }

    companion object {
        private const val TEST_GET_REQUESTS_RESOURCE = "requests/get-requests.http"
        private const val TEST_POST_REQUESTS_RESOURCE = "requests/post-requests.http"
        private const val TEST_REQUESTS_WITH_TESTS_RESOURCE = "requests/requests-with-tests.http"
    }
}
