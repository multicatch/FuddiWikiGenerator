package eu.fuddi

import eu.fuddi.rdf.*
import eu.fuddi.template.asWikiPages
import eu.fuddi.template.templateEngine
import eu.fuddi.wikimedia.WikiMediaConfig
import eu.fuddi.wikimedia.updatePages
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.RDFDataMgr

fun generateWiki(fileName: String, templateDirectory: String, wikiMediaConfig: WikiMediaConfig) {
    val model = RDFDataMgr.loadModel(fileName)
    model.compileWikiPages(templateDirectory)
            .asPageNameWithText(model.getNamespaceMap())
            .updatePages(wikiMediaConfig)
}

fun Model.compileWikiPages(templateDirectory: String): Sequence<Pair<URIRef, Map<String, String>>> {
    val namespaces = getNamespaceMap()
    val templateEngine = templateEngine()
    return fetchSubjectsInDefaultNamespace()
            .map { it.parseSubject(namespaces.values) }
            .map { it.uriRef to it.asWikiPages(templateEngine, templateDirectory, namespaces) }
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
