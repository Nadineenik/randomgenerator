package nadinee.randomgenerator.utils

import nadinee.randomgenerator.data.Repository
import nadinee.randomgenerator.model.Rule
import kotlin.random.Random

fun generateNumbers(
    min: Int,
    max: Int,
    count: Int,
    enabledSpecial: Boolean
): List<Int> {
    if (min >= max || count <= 0 || count > (max - min + 1)) {
        return emptyList()
    }

    val allNumbers = (min..max).toMutableList()

    if (!enabledSpecial || Repository.rules.isEmpty()) {
        allNumbers.shuffle()
        return allNumbers.take(count)
    }

    val result = MutableList(count) { 0 }
    val usedPositions = mutableSetOf<Int>()
    val remainingNumbers = allNumbers.toMutableList()

    for (rule in Repository.rules) {
        // Проверяем, что число в диапазоне
        if (rule.number !in min..max) continue

        val availablePositions = (rule.minPos..rule.maxPos)
            .filter { it in 1..count && it !in usedPositions }

        if (availablePositions.isEmpty()) {
            return emptyList()  // Конфликт — возвращаем пустой список
        }

        val chosenPos = availablePositions.random()
        result[chosenPos - 1] = rule.number
        usedPositions.add(chosenPos)
        remainingNumbers.remove(rule.number)
    }

    // Заполняем оставшиеся позиции
    remainingNumbers.shuffle()
    var index = 0
    for (i in 0 until count) {
        if (result[i] == 0) {
            if (index >= remainingNumbers.size) {
                return emptyList()  // На всякий случай — если что-то пошло не так
            }
            result[i] = remainingNumbers[index]
            index++
        }
    }

    return result
}