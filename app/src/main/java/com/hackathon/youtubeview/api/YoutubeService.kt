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

//    "snippet": {
//        "videoId": "lH2fvjHQSSA",
//        "topLevelComment": {
//            "kind": "youtube#comment",
//            "etag": "\"Bdx4f4ps3xCOOo1WZ91nTLkRZ_c/AiripCoe4Z_wNsyQmcvQhjnQ8l4\"",
//            "id": "UgwA4niyfWWc-Ucj9-x4AaABAg",
//            "snippet": {
//                "authorDisplayName": "Aaron O'Driscoll",
//                "authorProfileImageUrl": "https://yt3.ggpht.com/-kA8rweZM_E4/AAAAAAAAAAI/AAAAAAAAAAA/MM-MYhft1RE/s28-c-k-no-mo-rj-c0xffffff/photo.jpg",
//                "authorChannelUrl": "http://www.youtube.com/channel/UCqvbygVt3Tg4VuA9qfakMxw",
//                "authorChannelId": {
//                    "value": "UCqvbygVt3Tg4VuA9qfakMxw"
//                },
//                "videoId": "lH2fvjHQSSA",
//                "textDisplay": "I’m having a really hard time trying to progress with wheeling and other street tricks because I’m using my grans old bike and can’t get down to trails any tips on gettin of the feel of going over the bars or the back of the bike",
//                "textOriginal": "I’m having a really hard time trying to progress with wheeling and other street tricks because I’m using my grans old bike and can’t get down to trails any tips on gettin of the feel of going over the bars or the back of the bike",
//                "canRate": true,
//                "viewerRating": "none",
//                "likeCount": 0,
//                "publishedAt": "2019-08-02T08:31:39.000Z",
//                "updatedAt": "2019-08-02T08:31:39.000Z"
//            }
//        }
//    },

    @GET("https://www.googleapis.com/youtube/v3/commentThreads?textFormat=plainText&part=snippet&maxResults=100")
    fun getComments(@Query("videoId") id: String): Call<YTResponse>

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

    class YTComment {
        lateinit var id: String
        lateinit var snippet: YTSnippet
    }

    class YTSnippet {
        lateinit var title: String
        lateinit var description: String
        lateinit var publishedAt: Date
        lateinit var thumbnails: Map<String, YTThumbnail>
        lateinit var resourceId: YTResourceID
                 var topLevelComment: YTComment? = null

        var authorDisplayName: String? = null
        var authorProfileImageUrl: String? = null
        var textDisplay: String? = null
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
