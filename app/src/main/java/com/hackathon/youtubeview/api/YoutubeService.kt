package com.hackathon.youtubeview.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface YoutubeService {
    @GET("channels?part=contentDetails")
    fun getChannelInfo(@Query("id") id: String): Call<YTResponse>

    @GET("playlistItems?part=snippet&maxResults=50")
    fun getPlaylist(@Query("playlistId") id: String): Call<YTResponse>

    @GET("videos?part=contentDetails")
    fun getVideo(@Query("id") id: String): Call<YTResponse>

    class YTContentDetails {
        var relatedPlaylists: MutableMap<String, String>? = null
        var duration: String? = null
    }

    class YTThumbnail {
        lateinit var url: String
    }

    class YTResourceID {
        lateinit var videoId: String
    }

    class YTSnippet {
        lateinit var title: String
        lateinit var description: String
        lateinit var publishedAt: Date
        lateinit var thumbnails: Map<String, YTThumbnail>
        lateinit var resourceId: YTResourceID
    }

    class YTItem {
        lateinit var kind: String
        var contentDetails: YTContentDetails? = null
        var snippet: YTSnippet? = null
    }

    class YTResponse {
        lateinit var items: List<YTItem>
    }

    companion object {
        fun create(api_key: String): YoutubeService {
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
