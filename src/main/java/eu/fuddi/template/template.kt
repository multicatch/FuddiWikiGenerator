package eu.fuddi.template

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.FileLoader
import java.io.StringWriter

fun templateEngine(): PebbleEngine = PebbleEngine.Builder().loader(FileLoader()).build()

fun PebbleEngine.compileText(fileName: String, variables: Map<String, Any?>): String {
    val template = getTemplate(fileName)
    val writer = StringWriter()
    template.evaluate(writer, variables)
    return writer.toString()
}