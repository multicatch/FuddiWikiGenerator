package eu.fuddi.wikimedia

fun Sequence<Pair<String, String>>.updatePages(url: String, login: String, password: String) {
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