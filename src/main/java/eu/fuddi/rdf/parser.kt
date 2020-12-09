package eu.fuddi.rdf

import org.apache.jena.rdf.model.*

fun Model.fetchSubjectsInDefaultNamespace() = fetchSubjectsIn(fetchOntologyNamespace() ?: Namespace(""))

fun Model.fetchOntologyNamespace() = fetchOntologies().first().uri.let { ontologyUri ->
    getNamespaceMap().values.find {
        it.uri.dropLast(1) == ontologyUri
    }
}

fun Model.fetchOntologies() = listResourcesWithProperty(createProperty(RDF.uri, "type"), createResource(OWL["Ontology"].uri)).asSequence()

fun Model.fetchSubjectsIn(namespace: Namespace) = listSubjects().asSequence().filter {
    it.uri.startsWith(namespace.uri)
}

fun Model.getNamespaceMap(): Map<String, Namespace> = nsPrefixMap.mapValues { (_, value) ->
    Namespace(value)
}

fun Resource.parseSubject(namespaces: Collection<Namespace>): SubjectDescriptor {
    val properties = this.listProperties().toList()
            .map { it.toResourceProperty(namespaces) }
            .groupBy { it.predicate }

    return SubjectDescriptor(
            this.uri.toUriRef(namespaces),
            properties,
            (properties[RDF["type"]] ?: error("Subject $uri has no type!")).first().valueRef!!
    )
}

fun String.toUriRef(namespaces: Collection<Namespace>): URIRef {
    return namespaces.find {
        this.startsWith(it.uri)
    }?.let {
        URIRef(
                it,
                this.drop(it.uri.length)
        )
    } ?: error("$this is not in any bound namespace ($namespaces)")
}

fun Statement.toResourceProperty(namespaces: Collection<Namespace>) = SubjectProperty(
        this.predicate.uri.toUriRef(namespaces),
        this.`object`.takeIf { it.isResource }?.asResource()?.uri?.toUriRef(namespaces),
        this.`object`.takeIf { it.isLiteral }?.asLiteral()?.toValueLiteral(namespaces)
)

fun Literal.toValueLiteral(namespaces: Collection<Namespace>) = ValueLiteral(
        this.lexicalForm,
        this.language,
        this.datatypeURI.toUriRef(namespaces)
)

data class SubjectDescriptor(
        val uriRef: URIRef,
        val properties: Map<URIRef, List<SubjectProperty>>,
        val type: URIRef
)

data class SubjectProperty(
        val predicate: URIRef,
        val valueRef: URIRef?,
        val valueLiteral: ValueLiteral?
)

data class ValueLiteral(
        val value: String,
        val language: String,
        val datatype: URIRef
)