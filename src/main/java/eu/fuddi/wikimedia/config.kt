package eu.fuddi.wikimedia

data class WikiMediaConfig(
        val url: String,
        val login: String,
        val password: String
)

fun wikiMediaConfigOf(properties: Map<String, String>): WikiMediaConfig = WikiMediaConfig(
        url = properties["wikimedia.url"] ?: error("Cannot read wikimedia.url!"),
        login = properties["wikimedia.login"] ?: error("Cannot read wikimedia.login!"),
        password = properties["wikimedia.password"] ?: error("Cannot read wikimedia.password!")
)