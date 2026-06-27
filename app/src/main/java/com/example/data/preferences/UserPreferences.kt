package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _languageFlow = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "en") ?: "en")
    val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    private val _themeFlow = MutableStateFlow(prefs.getString(KEY_THEME, "system") ?: "system")
    val themeFlow: StateFlow<String> = _themeFlow.asStateFlow()

    private val _currencyFlow = MutableStateFlow(prefs.getString(KEY_CURRENCY, "USD") ?: "USD")
    val currencyFlow: StateFlow<String> = _currencyFlow.asStateFlow()

    private val _pinFlow = MutableStateFlow(prefs.getString(KEY_PIN, "") ?: "")
    val pinFlow: StateFlow<String> = _pinFlow.asStateFlow()

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _languageFlow.value = lang
    }

    fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
        _themeFlow.value = theme
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
        _currencyFlow.value = currency
    }

    fun setPin(pin: String) {
        prefs.edit().putString(KEY_PIN, pin).apply()
        _pinFlow.value = pin
    }

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_PIN = "pin"
    }
}
