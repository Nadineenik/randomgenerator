package nadinee.randomgenerator.data

import android.content.Context
import nadinee.randomgenerator.model.Rule

object Repository {
    private var _rules = mutableListOf<Rule>()
    private var _enabledSpecial = false

    val rules: List<Rule> get() = _rules.toList()
    val enabledSpecial: Boolean get() = _enabledSpecial

    fun init(context: Context) {
        _rules.clear()
        _rules.addAll(Storage.loadRules(context))
        _enabledSpecial = Storage.loadEnabled(context)
    }

    fun addRule(rule: Rule, context: Context) {
        _rules.add(rule)
        Storage.saveRules(context, _rules)
    }

    fun deleteRule(index: Int, context: Context) {
        if (index in _rules.indices) {
            _rules.removeAt(index)
            Storage.saveRules(context, _rules)
        }
    }

    fun setEnabled(enabled: Boolean, context: Context) {
        _enabledSpecial = enabled
        Storage.saveEnabled(context, enabled)
    }
}