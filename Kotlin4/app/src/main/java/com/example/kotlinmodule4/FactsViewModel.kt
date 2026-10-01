package com.example.kotlinmodule4

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FactsViewModel : ViewModel() {

    private val facts = listOf(
        "У бабочки вкус расположен на лапках.",
        "Белые медведи покрывают нос лапой, когда охотятся.",
        "Сердце креветки находится в голове.",
        "Вомбаты оставляют кубический помёт.",
        "У фламинго розовый цвет даёт пища, а не кожа.",
        "Кенгуру не могут прыгать назад.",
        "Утконос светится под ультрафиолетом.",
        "Голуби узнают себя в зеркале.",
        "У геккона лапы держатся за стекло силами Ван-дер-Ваальса.",
        "Капибары спокойно подпускают к себе других животных.",
        "У нарвала бивень — это вывернутый зуб.",
        "Страус развивает скорость около 70 км/ч.",
        "У осьминога зрачки прямоугольные.",
        "Пчёлы танцем сообщают направление к цветам.",
        "У альбатроса размах крыльев больше трёх метров."
    )

    fun getRandomFact(): Flow<String> = flow {

        val delayTime = Random.nextLong(1500, 3000)

        delay(delayTime)

        val fact = facts.random()

        emit(fact)
    }
}