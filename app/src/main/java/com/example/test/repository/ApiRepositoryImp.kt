package com.example.test.repository

import com.example.test.apiService.ApiServices
import javax.inject.Inject

class ApiRepositoryImp @Inject constructor(private var mApiServices: ApiServices) {

    suspend fun getCurrencyConverterRepo(currencyName: String) = mApiServices.getCurrencyConverterResult(currencyName)

}