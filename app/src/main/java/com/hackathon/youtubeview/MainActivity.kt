package com.hackathon.youtubeview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hackathon.youtubeview.api.YoutubeService
import com.hackathon.youtubeview.api.enqueue

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        syncVideos()
    }

    fun syncVideos() {
        val youtube = YoutubeService.Create(BuildConfig.API_KEY)
        val channel = BuildConfig.CHANNEL

        // We need to first get the "uploads" playlist ID from a channel ID.
        // Then we can get a list of videos in that playlist
        youtube.getChannelInfo(channel).enqueue { result ->
            val playlists = result.getOrNull()?.items?.getOrNull(0)?.contentDetails?.relatedPlaylists
            val uploadsPlaylistId = playlists?.get("uploads")
            if (uploadsPlaylistId == null) {
                return@enqueue
            }

            youtube.getPlaylist(uploadsPlaylistId).enqueue { result2 ->
                val items = result2.getOrNull()?.items
                items?.forEach {
                    Log.e("aaa", it.id)
                }
            }
        }
    }
}
