package eu.fuddi

import com.mitchellbosecke.pebble.PebbleEngine
import eu.fuddi.rdf.*
import eu.fuddi.template.compileText
import eu.fuddi.template.templateEngine
import eu.fuddi.wikimedia.WikiMediaConfig
import eu.fuddi.wikimedia.updatePages
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.RDFDataMgr

fun generateWiki(fileName: String, templateName: String, wikiMediaConfig: WikiMediaConfig) {
    val model = RDFDataMgr.loadModel(fileName)
    model.compileWikiPages(templateName)
            .asPageNameWithText(model.getNamespaceMap())
            .updatePages(wikiMediaConfig)
}

fun Model.compileWikiPages(templateName: String): Sequence<Pair<URIRef, Map<String, String>>> {
    val namespaces = getNamespaceMap()
    val templateEngine = templateEngine()
    return fetchSubjectsInDefaultNamespace()
            .map { it.parseSubject(namespaces.values) }
            .map { it.uriRef to it.asWikiPages(templateEngine, templateName, namespaces) }
}

fun Sequence<Pair<URIRef, Map<String, String>>>.asPageNameWithText(namespaces: Map<String, Namespace>): Sequence<Pair<String, String>> {
    val namespaceLookup = namespaces.map { (k, v) -> v to k }.toMap()

    return map { (key, articles) ->
        val nsName = namespaceLookup[key.namespace]
        val prefix = if (nsName.isNullOrEmpty()) "" else "$nsName:"
        val pageName = "$prefix${key.identifier}"

        articles.map { (language, text) ->
            val languageSuffix = if (language.isEmpty()) "" else "@$language"
            "$pageName$languageSuffix" to text
        }
    }.flatMap { it.asSequence() }
}

fun SubjectDescriptor.asWikiPages(pebbleEngine: PebbleEngine, templateName: String, namespaces: Map<String, Namespace>) = pebbleEngine.let {
    val namespaceLookup = namespaces.map { (name, namespace) ->
            namespace to name
    }.toMap()

    val propertiesByLanguage = properties.groupByLanguage()
    val subjectDescriptorMap = mapOf(
            "subjectUri" to uriRef,
            "subjectType" to type
    )
    val subjectVariables = namespaces + mapOf("nsName" to namespaceLookup) + subjectDescriptorMap

    propertiesByLanguage.mapValues { (_, properties) ->
        val propertyMap = mapOf("properties" to properties)
        it.compileText(
                templateName,
                subjectVariables + propertyMap
        )
    }
}

fun Map<URIRef, List<SubjectProperty>>.groupByLanguage(): Map<String, Map<URIRef, List<SubjectProperty>>> {
    val languages = getLanguages()
    return languages.map { language ->
        language to mapValues { (_, properties) ->
            properties.filter { it.valueLiteral == null || it.valueLiteral.language == language }
        }
    }.toMap()
}

fun Map<URIRef, List<SubjectProperty>>.getLanguages() = values.flatten()
        .mapNotNull { it.valueLiteral?.language }
        .toSet()