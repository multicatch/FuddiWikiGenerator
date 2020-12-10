package eu.fuddi.template

import com.mitchellbosecke.pebble.extension.Function
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import eu.fuddi.rdf.Namespace
import eu.fuddi.rdf.SubjectProperty
import eu.fuddi.rdf.URIRef
import eu.fuddi.rdf.ValueLiteral

private const val SUBJECT = "subject"

class WikiReadableFunction : Function {
    override fun getArgumentNames(): MutableList<String> {
        return mutableListOf(SUBJECT)
    }

    override fun execute(args: MutableMap<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): Any? {
        val namespaceLookup = context.getVariable(NS_LOOKUP_PROPERTY) as Map<Namespace, String>
        val language = context.getVariable(LANGUAGE_PROPERTY) as String?
        return when (val argument = args[SUBJECT]) {
            is SubjectProperty -> argument.asWikiReadable(namespaceLookup, language)
            is ValueLiteral -> textOf(namespaceLookup, argument, language)
            is URIRef -> wikiLinkOf(namespaceLookup, argument, language)
            else -> null
        }
    }

    private fun SubjectProperty.asWikiReadable(namespaceLookup: Map<Namespace, String>, language: String?) = if (valueRef != null) {
        wikiLinkOf(namespaceLookup, valueRef, language)
    } else {
        textOf(namespaceLookup, valueLiteral, language)
    }
}