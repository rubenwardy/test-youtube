package com.hackathon.youtubeview.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.logging.HttpLoggingInterceptor




interface YoutubeService {
    @GET("channels?part=contentDetails")
    fun getChannelInfo(@Query("id") id: String): Call<YTResponse>

    @GET("playlistItems?part=snippet&maxResults=50     ")
    fun getPlaylist(@Query("playlistId") id: String): Call<YTResponse>


    class YTContentDetails {
        lateinit var relatedPlaylists: MutableMap<String, String>

    }
    class YTItem {
        lateinit var kind: String
        lateinit var id: String
        var contentDetails: YTContentDetails? = null
    }


    class YTResponse {
        lateinit var items: List<YTItem>
    }


    companion object {
        fun Create(api_key: String): YoutubeService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val originalHttpUrl = original.url()

                    val url = originalHttpUrl.newBuilder()
                        .addQueryParameter("key", api_key)
                        .build()

                    val requestBuilder = original.newBuilder()
                        .url(url)

                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(YoutubeService::class.java)
        }
    }
}