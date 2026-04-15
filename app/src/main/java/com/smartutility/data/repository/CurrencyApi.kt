package com.smartutility.data.repository

import com.smartutility.data.models.ExchangeRateResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v6/latest/{base}")
    suspend fun getRates(@Path("base") base: String): Response<ExchangeRateResponse>
}

object CurrencyApi {
    private const val BASE_URL = "https://open.er-api.com/"

    val service: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}

// Fallback rates used when device is offline
val FALLBACK_RATES = mapOf(
    "USD" to 1.0,
    "EUR" to 0.92,
    "GBP" to 0.79,
    "KES" to 129.5,
    "JPY" to 149.8,
    "CNY" to 7.24,
    "INR" to 83.12,
    "AUD" to 1.53,
    "CAD" to 1.36,
    "CHF" to 0.90,
    "NGN" to 1560.0,
    "ZAR" to 18.63,
    "GHS" to 12.1,
    "EGP" to 30.9,
    "AED" to 3.67,
    "SAR" to 3.75,
    "BRL" to 4.97,
    "MXN" to 17.15,
    "SGD" to 1.34,
    "HKD" to 7.83,
    "SEK" to 10.42,
    "NOK" to 10.55,
    "DKK" to 6.89,
    "NZD" to 1.63,
    "MYR" to 4.72,
    "THB" to 35.1,
    "TRY" to 30.8,
    "PLN" to 4.02,
    "PKR" to 278.5,
    "BDT" to 110.0
)