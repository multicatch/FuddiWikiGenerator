package eu.fuddi.wikimedia

import org.slf4j.LoggerFactory

fun Sequence<Pair<String, String>>.updatePages(wikiMediaConfig: WikiMediaConfig) {
    val (url, login, password) = wikiMediaConfig
    WikiMediaClient(url).use { wikiMediaClient ->
        val loginToken = wikiMediaClient.fetchCsrfToken(TokenType.Login)
        val loginResult = wikiMediaClient.login(loginToken, login, password)
        logger.debug("Login RESULT: {}", loginResult)

        this.forEach { (title, text) ->
            wikiMediaClient.updatePage(title, text)
        }
    }
}

fun WikiMediaClient.updatePage(title: String, text: String) {
    val token = this.fetchCsrfToken(TokenType.CSRF)
    logger.debug("Sending request to edit {} with text {}", title, text)
    val result = this.editPost(token, title, text)
    logger.debug("Editing {} - RESULT: {}", title, result)
}

private val logger = LoggerFactory.getLogger(WikiMediaClient::class.java)
