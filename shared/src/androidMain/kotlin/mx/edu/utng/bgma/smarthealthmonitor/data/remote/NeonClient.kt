package mx.edu.utng.bgma.smarthealthmonitor.data.remote

import mx.edu.utng.bgma.smarthealthmonitor.shared.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NeonClient {
    private val BASE_URL = "https://${BuildConfig.NEON_HOST}/"
 
    val CONN_STRING  = BuildConfig.NEON_CONNECTION_STRING
 
    val api: NeonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build())
            .build()
            .create(NeonApiService::class.java)
    }
}
