package com.demo.covid19_dashboard.api

import com.demo.covid19_dashboard.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiHelperClass {

    companion object {

        private var instance: ApiCallInterface? = null
        private var apiCallInterface: ApiCallInterface? = null

        fun getInstance(): ApiCallInterface {

            if (instance == null) {
                val timeout = (30 * 1000).toLong()
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor)
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .addInterceptor { chain ->

                        val original: Request = chain.request()
                        val requestBuilder = original.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build()
                        chain.proceed(requestBuilder)
                    }
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS).build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                instance = retrofit.create(ApiCallInterface::class.java)
            }

            return instance as ApiCallInterface
        }

    }
}