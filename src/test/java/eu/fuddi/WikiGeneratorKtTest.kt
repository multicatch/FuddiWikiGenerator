package eu.fuddi

import com.mitchellbosecke.pebble.PebbleEngine
import eu.fuddi.rdf.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class WikiGeneratorKtTest {

    private val engine = PebbleEngine.Builder().build()
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
                    )
            ),
            OWL["Thing"]
    )

    @Test
    fun `should generate Wiki pages for subject descriptor`() {
        val expected = mapOf(
                "en" to "URI: http://example.org/subject\n" +
                        "Type: Thing\n" +
                        "Type NS: owl\n" +
                        "Label: English",
                "pl" to "URI: http://example.org/subject\n" +
                        "Type: Thing\n" +
                        "Type NS: owl\n" +
                        "Label: Polish"
        )

        val wikiPages = subject.asWikiPages(engine, "generator.txt", namespaces)

        Assertions.assertEquals(expected, wikiPages)
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
        val expected = listOf(
                "subject@en" to "URI: http://example.org/subject\n" +
                        "Type: Thing\n" +
                        "Type NS: owl\n" +
                        "Label: English",
                "subject@pl" to "URI: http://example.org/subject\n" +
                        "Type: Thing\n" +
                        "Type NS: owl\n" +
                        "Label: Polish"
        )

        val wikiPages = subject.asWikiPages(engine, "generator.txt", namespaces)

        val pages = sequenceOf(subject.uriRef to wikiPages)
                .asPageNameWithText(namespaces)
                .toList()

        Assertions.assertEquals(expected, pages)
    }
}