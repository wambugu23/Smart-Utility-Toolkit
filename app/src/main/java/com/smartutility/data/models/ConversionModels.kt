package com.smartutility.data.models

enum class ConversionCategory(val label: String, val units: List<ConversionUnit>) {
    LENGTH(
        label = "Length",
        units = listOf(
            ConversionUnit("Meter",      "m",   1.0),
            ConversionUnit("Kilometer",  "km",  0.001),
            ConversionUnit("Centimeter", "cm",  100.0),
            ConversionUnit("Millimeter", "mm",  1000.0),
            ConversionUnit("Mile",       "mi",  0.000621371),
            ConversionUnit("Yard",       "yd",  1.09361),
            ConversionUnit("Foot",       "ft",  3.28084),
            ConversionUnit("Inch",       "in",  39.3701)
        )
    ),
    WEIGHT(
        label = "Weight",
        units = listOf(
            ConversionUnit("Kilogram",  "kg", 1.0),
            ConversionUnit("Gram",      "g",  1000.0),
            ConversionUnit("Milligram", "mg", 1_000_000.0),
            ConversionUnit("Pound",     "lb", 2.20462),
            ConversionUnit("Ounce",     "oz", 35.274),
            ConversionUnit("Tonne",     "t",  0.001),
            ConversionUnit("Stone",     "st", 0.157473)
        )
    ),
    TEMPERATURE(
        label = "Temperature",
        units = listOf(
            ConversionUnit("Celsius",    "°C", 1.0),
            ConversionUnit("Fahrenheit", "°F", 1.0),
            ConversionUnit("Kelvin",     "K",  1.0)
        )
    ),
    SPEED(
        label = "Speed",
        units = listOf(
            ConversionUnit("m/s",   "m/s",  1.0),
            ConversionUnit("km/h",  "km/h", 3.6),
            ConversionUnit("mph",   "mph",  2.23694),
            ConversionUnit("Knot",  "kn",   1.94384),
            ConversionUnit("ft/s",  "ft/s", 3.28084)
        )
    )
}

data class ConversionUnit(
    val name: String,
    val symbol: String,
    val factorFromBase: Double
)

object ConversionEngine {

    fun convert(
        value: Double,
        from: ConversionUnit,
        to: ConversionUnit,
        category: ConversionCategory
    ): Double {
        if (category == ConversionCategory.TEMPERATURE) {
            return convertTemperature(value, from.symbol, to.symbol)
        }
        val inBase = value / from.factorFromBase
        return inBase * to.factorFromBase
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "°C" -> value
            "°F" -> (value - 32) * 5.0 / 9.0
            "K"  -> value - 273.15
            else -> value
        }
        return when (to) {
            "°C" -> celsius
            "°F" -> celsius * 9.0 / 5.0 + 32
            "K"  -> celsius + 273.15
            else -> celsius
        }
    }
}