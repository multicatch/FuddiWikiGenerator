package eu.fuddi.template

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.error.LoaderException
import com.mitchellbosecke.pebble.loader.FileLoader
import org.slf4j.LoggerFactory
import java.io.StringWriter

fun templateEngine(): PebbleEngine = PebbleEngine.Builder()
        .loader(FileLoader())
        .extension(FuddiExtension())
        .build()

fun PebbleEngine.compileText(directory: String, language: String, variables: Map<String, Any?>): String {
    val languageFile = language.toLowerCase() + ".txt"
    val fileName = "$directory$languageFile"

    val template = try {
        getTemplate(fileName)
    } catch(e: LoaderException) {
        val defaultFileName = "$directory$DEFAULT_FILE"
        logger.warn("Cannot load $fileName, defaulting to $defaultFileName.")
        getTemplate(defaultFileName)
    }

    val writer = StringWriter()
    template.evaluate(writer, variables)
    return writer.toString()
}

private const val DEFAULT_FILE = "default.txt"
private val logger = LoggerFactory.getLogger("eu.fuddi.template")