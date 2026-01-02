package nadinee.randomgenerator.utils

import nadinee.randomgenerator.data.Repository
import nadinee.randomgenerator.model.Rule
import kotlin.random.Random

/**
 * Генерирует список чисел с учётом особых условий (правил)
 */
fun generateNumbers(
    min: Int,
    max: Int,
    count: Int,
    enabledSpecial: Boolean
): List<Int> {
    // Проверка базовых условий
    if (min >= max || count <= 0 || count > (max - min + 1)) {
        return emptyList()
    }

    val allNumbers = (min..max).toMutableList()
    val result = MutableList(count) { 0 }

    if (!enabledSpecial || Repository.rules.isEmpty()) {
        // Простая случайная перетасовка
        allNumbers.shuffle()
        return allNumbers.take(count)
    }

    // === Обработка особых условий ===
    val usedPositions = mutableSetOf<Int>()
    val remainingNumbers = allNumbers.toMutableList()

    // Проходим по всем правилам и фиксируем позиции
    for (rule in Repository.rules) {
        if (rule.number !in allNumbers) continue  // число вне диапазона — игнорируем

        // Доступные позиции в диапазоне правила, которые ещё не заняты
        val availablePositions = (rule.minPos..rule.maxPos)
            .filter { it in 1..count && it !in usedPositions }

        if (availablePositions.isEmpty()) {
            // Конфликт — невозможно разместить
            return emptyList()  // можно потом показать ошибку
        }

        // Выбираем случайную доступную позицию
        val chosenPos = availablePositions.random()
        result[chosenPos - 1] = rule.number
        usedPositions.add(chosenPos)
        remainingNumbers.remove(rule.number)
    }

    // Заполняем оставшиеся позиции случайными числами
    remainingNumbers.shuffle()
    var index = 0
    for (i in 0 until count) {
        if (result[i] == 0) {
            result[i] = remainingNumbers[index++]
        }
    }

    return result
}