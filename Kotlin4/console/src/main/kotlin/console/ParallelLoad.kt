package console

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

suspend fun loadUsers(): List<String> {
    delay(1800)
    if (Random.nextBoolean()) {
        throw IllegalStateException("не удалось получить список пользователей")
    }
    return listOf("Марина", "Олег", "Кирилл", "Нина")
}

suspend fun loadSales(): Map<String, Int> {
    delay(1200)
    if (Random.nextBoolean()) {
        throw IllegalStateException("не удалось получить статистику продаж")
    }
    return mapOf("Круассан" to 27, "Какао" to 14, "Чай" to 19)
}

suspend fun loadWeather(): List<String> {
    delay(2500)
    if (Random.nextBoolean()) {
        throw IllegalStateException("не удалось получить погоду")
    }
    return listOf("Москва: -3°C", "Казань: -6°C", "Сочи: 8°C")
}

suspend fun <T> guarded(title: String, block: suspend () -> T, fallback: T): T {
    return try {
        block()
    } catch (error: Exception) {
        println("Сбой: $title. ${error.message}")
        fallback
    }
}

fun main(): Unit = runBlocking {
    println("Параллельная загрузка запущена")
    val time = measureTimeMillis {
        coroutineScope {
            val users = async {
                guarded("пользователи", ::loadUsers, emptyList())
            }
            val sales = async {
                guarded("продажи", ::loadSales, emptyMap())
            }
            val weather = async {
                guarded("погода", ::loadWeather, emptyList())
            }
            println("Пользователи: ${users.await()}")
            println("Продажи: ${sales.await()}")
            println("Погода: ${weather.await()}")
        }
    }
    println("Время выполнения: ${time / 1000.0} с")
}
