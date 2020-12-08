package eu.fuddi.rdf

import org.apache.jena.rdf.model.Model

fun Model.fetchSubjectsInDefaultNamespace() = nsPrefixMap.getOrDefault("", "")
        .let { prefix ->
            listSubjects().asSequence().filter {
                it.uri.startsWith(prefix)
            }
        }

fun Model.fetchSubjectsIn(nameSpace: String) = listSubjects().asSequence().filter {
    it.uri.startsWith(nameSpace)
}