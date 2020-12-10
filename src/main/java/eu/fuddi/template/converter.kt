package eu.fuddi.template

import com.mitchellbosecke.pebble.PebbleEngine
import eu.fuddi.rdf.Namespace
import eu.fuddi.rdf.SubjectDescriptor
import eu.fuddi.rdf.SubjectProperty
import eu.fuddi.rdf.URIRef

const val NS_LOOKUP_PROPERTY = "nsName"
const val LANGUAGE_PROPERTY = "language"

fun SubjectDescriptor.asWikiPages(pebbleEngine: PebbleEngine, templateDirectory: String, namespaces: Map<String, Namespace>) = pebbleEngine.let {
    val namespaceLookup = namespaces.map { (name, namespace) ->
        namespace to name
    }.toMap()

    val propertiesByLanguage = properties.groupByLanguage()
    val subjectDescriptorMap = mapOf(
            "subjectUri" to uriRef,
            "subjectType" to type
    )
    val subjectVariables = namespaces + mapOf(NS_LOOKUP_PROPERTY to namespaceLookup) + subjectDescriptorMap

    propertiesByLanguage.mapValues { (language, properties) ->
        val propertyMap = mapOf("properties" to properties, LANGUAGE_PROPERTY to language)
        it.compileText(
                templateDirectory,
                language,
                subjectVariables + propertyMap
        )
    }
}

fun Map<URIRef, List<SubjectProperty>>.groupByLanguage(): Map<String, Map<URIRef, List<SubjectProperty>>> {
    val languages = getLanguages()
    val result = languages.map { language ->
        language to mapValues { (_, properties) ->
            properties.filter { it.valueLiteral == null || it.valueLiteral.language.isNullOrBlank() || it.valueLiteral.language == language }
        }
    }.toMap()

    return when {
        result.size > 1 -> result.filterNot { (lang, _) -> lang.isBlank() }
        result.size == 1 -> result
        else -> mapOf("" to this)
    }
}

fun Map<URIRef, List<SubjectProperty>>.getLanguages() = values.flatten()
        .mapNotNull { it.valueLiteral?.language }
        .toSet()