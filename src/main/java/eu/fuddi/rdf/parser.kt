package eu.fuddi.rdf

import org.apache.jena.rdf.model.Literal
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.Statement

fun Model.fetchSubjectsInDefaultNamespace() = fetchSubjectsIn(getNamespaceMap().getOrDefault("", Namespace("")))

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