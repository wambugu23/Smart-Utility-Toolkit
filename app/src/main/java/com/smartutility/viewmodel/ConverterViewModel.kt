package com.smartutility.viewmodel

import androidx.lifecycle.ViewModel
import com.smartutility.data.models.ConversionCategory
import com.smartutility.data.models.ConversionEngine
import com.smartutility.data.models.ConversionUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConverterUiState(
    val selectedCategory: ConversionCategory = ConversionCategory.LENGTH,
    val fromUnit: ConversionUnit = ConversionCategory.LENGTH.units.first(),
    val toUnit: ConversionUnit = ConversionCategory.LENGTH.units[1],
    val inputValue: String = "",
    val result: String = ""
)

class ConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    fun selectCategory(category: ConversionCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                fromUnit  = category.units.first(),
                toUnit    = category.units.getOrElse(1) { category.units.first() },
                inputValue = "",
                result    = ""
            )
        }
    }

    fun setFromUnit(unit: ConversionUnit) {
        _uiState.update { it.copy(fromUnit = unit) }
        recalculate()
    }

    fun setToUnit(unit: ConversionUnit) {
        _uiState.update { it.copy(toUnit = unit) }
        recalculate()
    }

    fun onInputChange(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' || it == '-' }
        _uiState.update { it.copy(inputValue = filtered) }
        recalculate()
    }

    fun swapUnits() {
        _uiState.update { it.copy(fromUnit = it.toUnit, toUnit = it.fromUnit) }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val value = state.inputValue.toDoubleOrNull()
        if (value == null) {
            _uiState.update { it.copy(result = "") }
            return
        }
        val converted = ConversionEngine.convert(
            value    = value,
            from     = state.fromUnit,
            to       = state.toUnit,
            category = state.selectedCategory
        )
        val formatted = "%.6g".format(converted)
            .trimEnd('0')
            .trimEnd('.')
        _uiState.update { it.copy(result = formatted) }
    }
}