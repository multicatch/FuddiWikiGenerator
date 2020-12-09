package eu.fuddi.wikimedia

fun Sequence<Pair<String, String>>.updatePages(wikiMediaConfig: WikiMediaConfig) {
    val (url, login, password) = wikiMediaConfig
    WikiMediaClient(url).use { wikiMediaClient ->
        val loginToken = wikiMediaClient.fetchCsrfToken(TokenType.Login)
        wikiMediaClient.login(loginToken, login, password)

        this.forEach { (title, text) ->
            wikiMediaClient.updatePage(title, text)
        }
    }
}

fun WikiMediaClient.updatePage(title: String, text: String) {
    val token = this.fetchCsrfToken(TokenType.CSRF)
    this.editPost(token, title, text)
}
