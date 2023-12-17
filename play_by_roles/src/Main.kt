import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

fun main() {
    val rolesFile = "./res/roles.txt"
    val textLinesFile = "./res/textLines.txt"

    val roles = FileInputStream(rolesFile).use { stream ->
        InputStreamReader(stream, Charset.defaultCharset()).use { reader ->
            BufferedReader(reader).use { bufferedReader ->
                bufferedReader.readLines()
            }
        }
    }

    val roleLines = mutableMapOf<String, MutableList<String>>()

    FileInputStream(textLinesFile).use { stream ->
        InputStreamReader(stream, Charset.defaultCharset()).use { reader ->
            BufferedReader(reader).use { bufferedReader ->
                bufferedReader.forEachLine { line ->
                    val role = line.substringBefore(":")
                    val text = line.substringAfter(":").trim()
                    roleLines.computeIfAbsent(role) { mutableListOf() }.add(text)
                }
            }
        }
    }

    for (role in roles) {
        println()
        val roleName = role.substringBefore(":")
        val roleTexts = roleLines[roleName] ?: emptyList()

        println("$roleName:")
        roleTexts.forEachIndexed { index, text ->
            println("${index + 1}) $text")
        }
    }
}
