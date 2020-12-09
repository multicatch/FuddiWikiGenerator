package eu.fuddi.rdf

import org.apache.jena.riot.RDFDataMgr.loadModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ParserKtTest {
    @Test
    fun `should read ontology and retrieve all subjects in default namespace`() {
        val expected = listOf(
                "https://fuddi.eu/ontology/FFO/Food#hasAminoAcids",
                "https://fuddi.eu/ontology/FFO/Food#Nutrient",
                "https://fuddi.eu/ontology/FFO/Food#Mineral",
                "https://fuddi.eu/ontology/FFO/Food#water",
                "https://fuddi.eu/ontology/FFO/Food#portionOfApple",
                "https://fuddi.eu/ontology/FFO/Food#hasMinerals",
                "https://fuddi.eu/ontology/FFO/Food#UnitOfQuantity",
                "https://fuddi.eu/ontology/FFO/Food#hasEssentialNutrients",
                "https://fuddi.eu/ontology/FFO/Food#UnitOfMass",
                "https://fuddi.eu/ontology/FFO/Food#AscorbicAcid",
                "https://fuddi.eu/ontology/FFO/Food#milligrams",
                "https://fuddi.eu/ontology/FFO/Food#Water",
                "https://fuddi.eu/ontology/FFO/Food#hasVitaminC",
                "https://fuddi.eu/ontology/FFO/Food#hasQuantity",
                "https://fuddi.eu/ontology/FFO/Food#AminoAcid",
                "https://fuddi.eu/ontology/FFO/Food#UnitOfMeasurement",
                "https://fuddi.eu/ontology/FFO/Food#FattyAcid",
                "https://fuddi.eu/ontology/FFO/Food#hasFattyAcids",
                "https://fuddi.eu/ontology/FFO/Food#hasNutrients",
                "https://fuddi.eu/ontology/FFO/Food#grams",
                "https://fuddi.eu/ontology/FFO/Food#UnitOfVolume",
                "https://fuddi.eu/ontology/FFO/Food#hasVitamins",
                "https://fuddi.eu/ontology/FFO/Food#hasVolume",
                "https://fuddi.eu/ontology/FFO/Food#Vitamin",
                "https://fuddi.eu/ontology/FFO/Food#kilograms",
                "https://fuddi.eu/ontology/FFO/Food#EssentialNutrient",
                "https://fuddi.eu/ontology/FFO/Food#Apple",
                "https://fuddi.eu/ontology/FFO/Food#hasMass",
                "https://fuddi.eu/ontology/FFO/Food#Food"
        )

        val subjects = loadModel("data.rdf")
                .fetchSubjectsInDefaultNamespace()
                .toList()
                .map { it.uri }

        Assertions.assertEquals(expected, subjects)
    }

    @Test
    fun `should read ontology and parse subject`() {
        val namespace = Namespace("https://fuddi.eu/ontology/FFO/Food#")
        val expected = SubjectDescriptor(
                namespace["hasAminoAcids"],
                mapOf(
                        RDFS["seeAlso"] to listOf(
                                SubjectProperty(RDFS["seeAlso"], namespace["AminoAcid"], null)
                        ),
                        RDFS["label"] to listOf(
                                SubjectProperty(RDFS["label"], null,
                                        ValueLiteral(
                                                "has amino acids",
                                                "en",
                                                RDF["langString"]
                                        )
                                )
                        ),
                        RDFS["subPropertyOf"] to listOf(
                                SubjectProperty(RDFS["subPropertyOf"], namespace["hasEssentialNutrients"], null)
                        ),
                        RDF["type"] to listOf(
                                SubjectProperty(RDF["type"], OWL["DatatypeProperty"], null)
                        )
                ),
                OWL["DatatypeProperty"]
        )

        val model = loadModel("data.rdf")
        val subject = model
                .fetchSubjectsInDefaultNamespace()
                .toList()
                .first()
                .parseSubject(model.getNamespaceMap().values)

        Assertions.assertEquals(expected, subject)
    }


}