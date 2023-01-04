package com.example.test.appModel

import android.app.Application
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log
import com.example.test.repository.ApiRepositoryImp
import com.example.test.apiService.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModel {

    private var mTag = AppModel::class.java.simpleName

    @Provides
    @Singleton
    fun getContext() = AppController.getContext()


    @Provides
    @Singleton
    fun provideRepoInstance(mApiServices: ApiServices) = ApiRepositoryImp(mApiServices)

    @Provides
    @Singleton
    fun provideApiServiceInstance(mRetrofit: Retrofit): ApiServices =
        mRetrofit.create(ApiServices::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://open.er-api.com/")
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(application: Application): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val cacheDir = File(application.cacheDir, UUID.randomUUID().toString())
        // 10 MiB cache
        val cache = Cache(cacheDir, 10 * 1024 * 1024)
        return OkHttpClient.Builder()
            .cache(cache)
//            .addInterceptor(httpLoggingInterceptor()!!)
//            .addInterceptor(EncryptionInterceptor())
//            .addInterceptor(DecryptionInterceptor())
//            .addNetworkInterceptor(networkInterceptor()!!) // only used when network is on
//            .addInterceptor(offlineInterceptor()!!) // // only used when network is ff
//            .build()

            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(networkInterceptor()!!) // only used when network is on
            .addInterceptor(offlineInterceptor()!!)
            .build()
    }


    //This interceptor will be called ONLY if the network is available
    private fun networkInterceptor(): Interceptor? {
        return object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.e(mTag, "network is on interceptor: called.")

                val response = chain.proceed(chain.request())
//                Log.e(mTag,"Response ${response.headers}")
                if (isConnect()) {
                    val cacheControl: CacheControl = CacheControl.Builder()
                        .maxAge(5, TimeUnit.SECONDS)
                        .build()
                    return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", cacheControl.toString())
                        .build()
                    return response
                }
                return response
            }
        }
    }

    // This interceptor will be called both if the network is available and if the network is not available
    private fun offlineInterceptor(): Interceptor? {
        return object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.e(mTag, "network is off interceptor: called.")
                var request = chain.request()

                if (!isConnect()) {
                    val cacheControl: CacheControl = CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build()
                    request = request.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .cacheControl(cacheControl)
                        .build()
                }

                return chain.proceed(request)
            }
        }
    }

    private fun isConnect(): Boolean {
        val connectivityManager =
            getContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false
    }


    private fun httpLoggingInterceptor(): HttpLoggingInterceptor? {
        val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
//                Log.e(mTag, "log: http log: $message");
            }
        })
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }

}




