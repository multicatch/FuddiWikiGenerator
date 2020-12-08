package eu.fuddi

import eu.fuddi.rdf.fetchSubjectsIn
import eu.fuddi.rdf.fetchSubjectsInDefaultNamespace
import org.apache.jena.riot.RDFDataMgr

fun generateWiki(fileName: String) {
    val model = RDFDataMgr.loadModel(fileName)
    val subjects = model.fetchSubjectsInDefaultNamespace()
}
