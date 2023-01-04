package com.example.test.apiService

import com.example.test.dataSource.CurrencyPojo
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

//     @GET("v6/latest/USD")
//    suspend fun getCurrencyConverterResult(): Response<CurrencyPojo>

    @GET("v6/latest/{name}")
    suspend fun getCurrencyConverterResult(@Path("name") name :String ): Response<CurrencyPojo>

}