package eu.fuddi.template

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TemplateKtTest {
    private val engine = templateEngine()

    @Test
    fun `should compile template`() {
        val text = engine.compileText("template.txt", mapOf(
                "name" to "John Doe"
        ))

        Assertions.assertEquals("Hello there, John Doe!", text)
    }
}