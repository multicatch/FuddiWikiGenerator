package eu.fuddi.template

import com.mitchellbosecke.pebble.extension.Function
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import eu.fuddi.rdf.Namespace
import eu.fuddi.rdf.URIRef

private const val URI = "uri"

class WikiLinkFunction : Function {
    override fun getArgumentNames(): MutableList<String> {
        return mutableListOf(URI)
    }

    override fun execute(args: MutableMap<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): Any? {
        val namespaceLookup = context.getVariable(NS_LOOKUP_PROPERTY) as Map<Namespace, String>
        val language = context.getVariable(LANGUAGE_PROPERTY) as String?
        val uri = when (val uri = args[URI]) {
            is URIRef -> uri
            else -> null
        } ?: return null

        return wikiLinkOf(namespaceLookup, uri, language)
    }
}

fun wikiLinkOf(namespaceLookup: Map<Namespace, String>, uri: URIRef, language: String?): String {
    val namespace = namespaceLookup[uri.namespace]
    val langRef = if (language.isNullOrBlank()) "" else "@$language"
    return when {
        namespace == null -> {
            uri.uri
        }
        namespace.isBlank() -> {
            "[[${uri.identifier}$langRef]]"
        }
        else -> {
            "[[$namespace:${uri.identifier}$langRef]]"
        }
    }
}