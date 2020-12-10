package eu.fuddi.template

import com.mitchellbosecke.pebble.extension.AbstractExtension
import com.mitchellbosecke.pebble.extension.Function

class FuddiExtension : AbstractExtension() {

    override fun getFunctions(): MutableMap<String, Function> {
        return mutableMapOf(
                "printLiteral" to PrintLiteralFunction(),
                "wikiLink" to WikiLinkFunction(),
                "wikiReadable" to WikiReadableFunction()
        )
    }
}