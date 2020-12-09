package eu.fuddi.rdf

val RDF = Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
val RDFS = Namespace("http://www.w3.org/2000/01/rdf-schema#")
val OWL = Namespace("http://www.w3.org/2002/07/owl#")

data class URIRef(
        val namespace: Namespace,
        val identifier: String
) {
    val uri: String = "$namespace$identifier"
}

data class Namespace(
        val uri: String
) {
    operator fun get(name: String): URIRef = URIRef(this, name)
}