package eu.fuddi.template

import com.mitchellbosecke.pebble.PebbleEngine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TemplateKtTest {
    private val engine = PebbleEngine.Builder().build()

    @Test
    fun `should compile template`() {
        val text = engine.compileText("templates/", "en", mapOf(
                "name" to "John Doe"
        ))

        Assertions.assertEquals("Hello there, John Doe!", text)
    }

    @Test
    fun `should compile default template when there is no language`() {
        val text = engine.compileText("templates/", "pl", mapOf(
                "name" to "John Doe"
        ))

        Assertions.assertEquals("Default template, John Doe.", text)
    }
}