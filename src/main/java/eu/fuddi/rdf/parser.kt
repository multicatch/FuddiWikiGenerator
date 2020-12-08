package eu.fuddi.rdf

import org.apache.jena.rdf.model.Model

fun Model.subjectsIn(nameSpace: String) = listSubjects().asSequence().filter {
    it.uri.startsWith(nameSpace)
}