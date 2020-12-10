package eu.fuddi.template

import com.mitchellbosecke.pebble.extension.Function
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import eu.fuddi.rdf.Namespace
import eu.fuddi.rdf.RDF
import eu.fuddi.rdf.SubjectProperty
import eu.fuddi.rdf.ValueLiteral

private const val LITERAL = "literal"

class PrintLiteralFunction : Function {
    override fun getArgumentNames(): MutableList<String> {
        return mutableListOf(LITERAL)
    }

    override fun execute(args: MutableMap<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): Any? {
        val namespaceLookup = context.getVariable(NS_LOOKUP_PROPERTY) as Map<Namespace, String>
        val language = context.getVariable(LANGUAGE_PROPERTY) as String?
        val literal = when (val argument = args[LITERAL]) {
            is ValueLiteral -> argument
            is SubjectProperty -> argument.valueLiteral
            else -> null
        }

        return textOf(namespaceLookup, literal, language)
    }
}

fun textOf(namespaceLookup: Map<Namespace, String>, literal: ValueLiteral?, language: String?): String? {
    if (literal == null) {
        return null
    }

    val datatype = literal.datatype
    if (datatype == RDF["langString"]) {
        return literal.value
    }

    return "${literal.value} (${wikiLinkOf(namespaceLookup, literal.datatype, literal.language ?: language)})"
}