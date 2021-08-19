package com.arvi.RetrofitApiCall

import android.util.Log
import com.arvi.Utils.AppConstants
import com.arvi.Utils.AppConstants.BASE_Custom_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiDynamicUtils {

    val apiService: APIService
        get() = getClient(BASE_Custom_URL!!)!!.create(APIService::class.java)




    fun getClient(baseUrl: String): Retrofit? {
        var retrofit: Retrofit? = null
        Log.e("BASE_Custom_URL: ", baseUrl)
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        if (retrofit == null) {
            //TODO While release in Google Play Change the Level to NONE
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_Custom_URL!!)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        return retrofit

    }
}