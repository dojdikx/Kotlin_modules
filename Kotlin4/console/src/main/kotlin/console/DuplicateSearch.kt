package console

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.security.MessageDigest

suspend fun sha256(file: File): String = withContext(Dispatchers.IO) {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(file.readBytes())
    digest.digest().joinToString("") { "%02x".format(it) }
}

fun main() = runBlocking {
    val root = File("console/sample-files")
    val timeoutSeconds = 5L
    println("Ищем дубликаты в ${root.path}")

    val groups = withTimeoutOrNull(timeoutSeconds * 1000) {
        val files = root.walkTopDown()
            .filter { it.isFile && it.extension.equals("json", ignoreCase = true) }
            .toList()
        files.map { file ->
            async { file to sha256(file) }
        }.awaitAll()
            .groupBy { it.second }
            .filter { it.value.size > 1 }
    }

    if (groups == null) {
        println("Поиск прерван по таймауту")
        return@runBlocking
    }
    if (groups.isEmpty()) {
        println("Дубликаты не найдены")
        return@runBlocking
    }
    groups.forEach { (hash, files) ->
        println("SHA-256 ${hash.take(12)}…")
        files.forEach { (file, _) -> println("  ${file.path}") }
    }
}
