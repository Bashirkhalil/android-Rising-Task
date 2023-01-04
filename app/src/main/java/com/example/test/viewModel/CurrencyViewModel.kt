package com.example.test.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.test.repository.ApiRepositoryImp
import com.example.test.dataSource.CurrencyPojo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private var mApiRepositoryImp: ApiRepositoryImp) :  ViewModel() {

  private var mJob: Job? = null

  private val mTag = CurrencyViewModel::class.java.simpleName
  private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
    throwable.printStackTrace()
    Log.e(mTag, "coroutineExceptionHandler -> ${throwable.message}")
  }


  /*
    Response
   */
  var currencyResponse = MutableLiveData<CurrencyPojo>()
  var currencyResponseError = MutableLiveData<String>()

  fun getCurrenyConvertData(currencyName:String) {
    mJob = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
      var responseResult = mApiRepositoryImp.getCurrencyConverterRepo(currencyName)
      withContext(Dispatchers.Main) {
        if (responseResult.isSuccessful) {
//          Log.e(mTag,"Resullit is ${responseResult.body()}")
          currencyResponse.value = responseResult.body()
        } else {
          currencyResponseError.value = responseResult.message()
        }
      }
    }
  }
    /*
    Response
    */
    var currencyResponseValue = MutableLiveData<CurrencyPojo>()
    var currencyResponseErrorValue = MutableLiveData<String>()

    fun getCurrentConvertValue(currencyName:String) {
    mJob = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
      var responseResult = mApiRepositoryImp.getCurrencyConverterRepo(currencyName)
      withContext(Dispatchers.Main) {
        if (responseResult.isSuccessful) {
//          Log.e(mTag,"Resullit is ${responseResult.body()}")
          currencyResponseValue.value = responseResult.body()
        } else {
          currencyResponseErrorValue.value = responseResult.message()
        }
      }
    }
  }

}