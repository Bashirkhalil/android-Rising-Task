package com.example.test.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityMainBinding
import com.example.test.viewModel.CurrencyViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mTag = MainActivity::class.java.simpleName
    var  currrentValuePosition: Int = 0


    private lateinit var binding: ActivityMainBinding
    private val mCurrencyViewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** init first data with USD currency */
        mCurrencyViewModel.getCurrenyConvertData("USD")
        mCurrencyViewModel.currencyResponse.observe(this) {

            var jsonString = Gson().toJson(it.rates)

            val currency = JSONObject(jsonString)
            val x: Iterator<*> = currency.keys()

            val currencyValue: ArrayList<String> = ArrayList()
            val currencyName:  ArrayList<String> = ArrayList()

            while (x.hasNext()) {
                val key = x.next() as String
                currencyName.add(key)
                currencyValue.add(currency[key].toString())
            }
            addToTopSpinner(currencyName,currencyValue)
        }

        mCurrencyViewModel.currencyResponseError.observe(this) {
            Log.e(mTag,"Error is occur ${it}")
        }



        /* init Top Spinner */
    }

    private fun getCurrencyValue(currencyName: String) {

        mCurrencyViewModel.getCurrentConvertValue(currencyName)
        mCurrencyViewModel.currencyResponseValue.observe(this) {
            Log.e(mTag,"Result is ${it}")

            var jsonString = Gson().toJson(it.rates)

            val currency = JSONObject(jsonString)
            val x: Iterator<*> = currency.keys()

            val currencyValue: ArrayList<String> = ArrayList()
            val currencyName:  ArrayList<String> = ArrayList()

            while (x.hasNext()) {
                val key = x.next() as String
                currencyName.add(key)
                currencyValue.add(currency[key].toString())
            }
            addToSpinnerCurrencyResultValue(currencyName,currencyValue)
        }
        mCurrencyViewModel.currencyResponseErrorValue.observe(this) {
            Log.e(mTag,"Error is ${it}")
        }
    }

    private fun addToSpinnerCurrencyResultValue(currencyName: java.util.ArrayList<String>, currencyValue: java.util.ArrayList<String>) {
        try {
            // upper currency
            binding.spinnerB.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencyName)
            binding.spinnerB.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    binding.valueB.setText(currencyValue.get(position))
                    currrentValuePosition = position
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

            binding.valueA.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    Log.e(mTag,"New $s")

                    if(s.isNotEmpty()){
                        var userValue =   s.toString().toInt()
                        var currentV = currencyValue.get(currrentValuePosition)
                        var result  = currentV.toDouble() * userValue

                        Log.e(mTag,"userValue = {${userValue} result = ${result}  -  Values is ${currentV}")

                        binding.valueB.setText(result.toString())
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addToTopSpinner(currencyName: ArrayList<String>, currencyValue: ArrayList<String>) {

        try {
            binding.spinnerA.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencyName)
            binding.spinnerA.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    getCurrencyValue(currencyName.get(p2))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

            binding.valueA.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                 Log.e(mTag,"New $s")

                    if(s.isNotEmpty()){
                        var userValue =   s.toString().toInt()
                        var currentV = currencyValue.get(currrentValuePosition)
                        var result  = currentV.toString().toDouble() * userValue

                        Log.e(mTag,"userValue = {${userValue} result = ${result}  -  Values is ${currentV}")

                        binding.valueB.setText(result.toString())
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}