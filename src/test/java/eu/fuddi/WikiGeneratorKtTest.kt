package eu.fuddi

import com.mitchellbosecke.pebble.PebbleEngine
import eu.fuddi.rdf.*
import eu.fuddi.template.FuddiExtension
import eu.fuddi.template.asWikiPages
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class WikiGeneratorKtTest {

    private val expected = listOf(
            "en" to "URI: http://example.org/subject\n" +
                    "Link: [[subject@en]]\n" +
                    "Type: Thing\n" +
                    "Type NS: owl\n" +
                    "Label: English\n" +
                    "\n" +
                    "[[rdf:type@en]]\n" +
                    "    [[owl:Thing@en]]\n" +
                    "\n" +
                    "[[rdfs:label@en]]\n" +
                    "    English\n" +
                    "    Unknown\n" +
                    "\n" +
                    "[[rdfs:isDefinedBy@en]]\n" +
                    "    2.2 (http://www.w3.org/2001/XMLSchema#double)\n" +
                    "    2.2 ([[double@en]])\n",
            "pl" to "URI: http://example.org/subject\n" +
                    "Link: [[subject@pl]]\n" +
                    "Type: Thing\n" +
                    "Type NS: owl\n" +
                    "Label: Polish\n" +
                    "\n" +
                    "[[rdf:type@pl]]\n" +
                    "    [[owl:Thing@pl]]\n" +
                    "\n" +
                    "[[rdfs:label@pl]]\n" +
                    "    Polish\n" +
                    "    Unknown\n" +
                    "\n" +
                    "[[rdfs:isDefinedBy@pl]]\n" +
                    "    2.2 (http://www.w3.org/2001/XMLSchema#double)\n" +
                    "    2.2 ([[double@pl]])\n"
    )

    private val engine = PebbleEngine.Builder()
            .extension(FuddiExtension())
            .build()

    private val namespace = Namespace("http://example.org/")
    private val namespaces = mapOf(
            "" to namespace,
            "rdf" to RDF,
            "rdfs" to RDFS,
            "owl" to OWL
    )
    private val subject = SubjectDescriptor(
            namespace["subject"],
            mapOf(
                    RDF["type"] to listOf(
                            SubjectProperty(RDF["type"], OWL["Thing"], null)
                    ),
                    RDFS["label"] to listOf(
                            SubjectProperty(RDFS["label"], null, ValueLiteral("English", "en", RDF["langString"])),
                            SubjectProperty(RDFS["label"], null, ValueLiteral("Polish", "pl", RDF["langString"])),
                            SubjectProperty(RDFS["label"], null, ValueLiteral("Unknown", "", RDF["langString"]))
                    ),
                    RDFS["isDefinedBy"] to listOf(
                            SubjectProperty(RDFS["isDefinedBy"], null, ValueLiteral("2.2", null, XSD["double"])),
                            SubjectProperty(RDFS["isDefinedBy"], null, ValueLiteral("2.2", null, namespace["double"]))
                    )
            ),
            OWL["Thing"]
    )

    @Test
    fun `should generate Wiki pages for subject descriptor`() {
        val wikiPages = subject.asWikiPages(engine, "generator/", namespaces)

        Assertions.assertEquals(expected.toMap(), wikiPages)
    }

    @Test
    fun `should generate titles with prefixes`() {
        val expected = listOf(
                "owl:subject" to "Unknown",
                "owl:subject@en" to "English"
        )

        val wikiPages = mapOf("" to "Unknown", "en" to "English")

        val pages = sequenceOf(OWL["subject"] to wikiPages)
                .asPageNameWithText(namespaces)
                .toList()

        Assertions.assertEquals(expected, pages)
    }

    @Test
    fun `should generate Wiki pages and generate titles`() {
        val expected = this.expected.map { (k, v) -> "subject@$k" to v }

        val wikiPages = subject.asWikiPages(engine, "generator/", namespaces)

        val pages = sequenceOf(subject.uriRef to wikiPages)
                .asPageNameWithText(namespaces)
                .toList()

        Assertions.assertEquals(expected, pages)
    }
}