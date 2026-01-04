package nadinee.randomgenerator.data

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import nadinee.randomgenerator.model.Rule
import java.io.File

object Storage {
    private const val FILE_RULES = "rules.json"
    private const val FILE_ENABLED = "enabled.txt"

    private val json = Json { prettyPrint = true }

    fun saveRules(context: Context, rules: List<Rule>) {
        val file = File(context.filesDir, FILE_RULES)
        file.writeText(json.encodeToString<List<Rule>>(rules))  // ← Добавь <List<Rule>>
    }

    fun loadRules(context: Context): List<Rule> {
        val file = File(context.filesDir, FILE_RULES)
        return if (file.exists()) {
            json.decodeFromString<List<Rule>>(file.readText())  // ← Добавь <List<Rule>>
        } else emptyList()
    }

    // enabled без изменений
    fun saveEnabled(context: Context, enabled: Boolean) {
        val file = File(context.filesDir, FILE_ENABLED)
        file.writeText(enabled.toString())
    }

    fun loadEnabled(context: Context): Boolean {
        val file = File(context.filesDir, FILE_ENABLED)
        return if (file.exists()) file.readText().toBoolean() else false
    }
}