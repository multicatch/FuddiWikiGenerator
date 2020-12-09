package eu.fuddi.wikimedia

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import java.io.Closeable

class WikiMediaClient(
        private val url: String,
        private val client: CloseableHttpClient = client(),
        private val context: HttpContext = defaultContext(),
        private val objectMapper: ObjectMapper = jacksonObjectMapper()
): Closeable {
    fun fetchCsrfToken(type: TokenType): String {
        val uri = URIBuilder(url).also { uri ->
            uri.setParameter("action", "query")
            uri.setParameter("meta", "tokens")
            if (type.type != null) {
                uri.setParameter("type", type.type)
            }
            uri.setParameter("format", "json")
        }.build()

        val response = request(HttpGet(uri))

        val responseNode = objectMapper.readValue<JsonNode>(String(response))
        return responseNode["query"]["tokens"][type.responseField].textValue()
    }

    fun login(token: String, login: String, password: String): String {
        val postEntity = postEntityOf(mapOf(
                "action" to "login",
                "lgname" to login,
                "lgpassword" to password,
                "lgtoken" to token,
                "format" to "json"
        ))

        val post = HttpPost(url).also {
            it.entity = postEntity
        }

        return String(request(post))
    }

    fun editPost(token: String, title: String, text: String): String {
        val postEntity = postEntityOf(mapOf(
                "action" to "edit",
                "title" to title,
                "token" to token,
                "format" to "json",
                "text" to text,
                "bot" to "true"
        ))

        val post = HttpPost(url).also {
            it.entity = postEntity
        }

        return String(request(post))
    }

    private fun request(request: HttpUriRequest) = client.let {
        val response = it.execute(request, context)
        response.expect(200)
        response.entity.content.readBytes()
    }

    private fun postEntityOf(params: Map<String, String>) = MultipartEntityBuilder.create()
            .apply {
                params.forEach { (key, value) ->
                    addTextBody(key, value)
                }
            }.build()

    override fun close() {
        client.close()
    }
}

enum class TokenType(val type: String?, val responseField: String) {
    Login("login", "logintoken"),
    CSRF(null, "csrftoken")
}

fun client(): CloseableHttpClient = HttpClients.createDefault()

fun defaultContext(): HttpContext = BasicHttpContext().apply {
    setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore());
}

fun HttpResponse.expect(status: Int) {
    val statusCode = this.statusLine.statusCode
    if (statusCode != status) {
        error("Expected $status, but server responded with $statusCode")
    }
}
