package com.smartutility.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartutility.data.repository.CurrencyApi
import com.smartutility.data.repository.FALLBACK_RATES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CurrencyUiState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "KES",
    val inputAmount: String = "1",
    val result: String = "",
    val rates: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val lastUpdated: String = ""
)

class CurrencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    val allCurrencies: List<String> = FALLBACK_RATES.keys.sorted()

    init {
        fetchRates()
    }

    fun fetchRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = CurrencyApi.service.getRates("USD")
                if (response.isSuccessful && response.body() != null) {
                    val now = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                        .format(Date())
                    _uiState.update {
                        it.copy(
                            rates       = response.body()!!.rates,
                            isLoading   = false,
                            isOffline   = false,
                            lastUpdated = "Live · $now"
                        )
                    }
                } else {
                    loadFallback()
                }
            } catch (e: Exception) {
                loadFallback()
            }
            recalculate()
        }
    }

    private fun loadFallback() {
        _uiState.update {
            it.copy(
                rates       = FALLBACK_RATES,
                isLoading   = false,
                isOffline   = true,
                lastUpdated = "Offline rates"
            )
        }
    }

    fun setFromCurrency(currency: String) {
        _uiState.update { it.copy(fromCurrency = currency) }
        recalculate()
    }

    fun setToCurrency(currency: String) {
        _uiState.update { it.copy(toCurrency = currency) }
        recalculate()
    }

    fun onInputChange(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(inputAmount = filtered) }
        recalculate()
    }

    fun swapCurrencies() {
        _uiState.update {
            it.copy(fromCurrency = it.toCurrency, toCurrency = it.fromCurrency)
        }
        recalculate()
    }

    private fun recalculate() {
        val state    = _uiState.value
        val amount   = state.inputAmount.toDoubleOrNull() ?: return
        val rates    = state.rates
        if (rates.isEmpty()) return

        val fromRate = rates[state.fromCurrency] ?: return
        val toRate   = rates[state.toCurrency]   ?: return

        val converted = (amount / fromRate) * toRate
        val formatted = when {
            converted >= 1_000_000 -> "%.2f M".format(converted / 1_000_000)
            converted >= 0.01      -> "%.4f".format(converted)
                .trimEnd('0').trimEnd('.')
            else                   -> "%.8f".format(converted)
                .trimEnd('0').trimEnd('.')
        }
        _uiState.update { it.copy(result = formatted) }
    }
}