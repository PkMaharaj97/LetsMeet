package com.example.letsmeet.addevent
import retrofit2.Retrofit

object ApiService {
    private val TAG = "--ApiService"

    private const val BASE_URL = "http://192.168.43.162/LetsMeet/"

    fun loginApiCall() = Retrofit.Builder()
        .baseUrl(BASE_URL)
         //   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(ApiWorker.gsonConverter)
        .client(ApiWorker.client)
        .build()
        .create(ApiInterface::class.java)!!
}
