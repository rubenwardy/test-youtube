package com.hackathon.youtubeview.presenter

import android.support.annotation.StringRes
import com.hackathon.youtubeview.BuildConfig
import com.hackathon.youtubeview.R
import com.hackathon.youtubeview.api.YoutubeService
import com.hackathon.youtubeview.api.enqueue
import com.hackathon.youtubeview.model.Video
import io.realm.Realm

class MainPresenter(private val view: View) {
    fun syncVideos() {
        val youtube = YoutubeService.create(BuildConfig.API_KEY)
        val channel = BuildConfig.CHANNEL

        // We need to first get the "uploads" playlist ID from a channel ID.
        // Then we can get a list of videos in that playlist
        youtube.getChannelInfo(channel).enqueue { result ->
            val playlists = result.getOrNull()?.items?.getOrNull(0)?.contentDetails?.relatedPlaylists
            val uploadsPlaylistId = playlists?.get("uploads")
            if (uploadsPlaylistId == null) {
                view.showError(R.string.unable_uploads)
                return@enqueue
            }

            youtube.getPlaylist(uploadsPlaylistId).enqueue { result2 ->
                val items = result2.getOrNull()?.items
                Realm.getDefaultInstance().executeTransaction { realm ->
                    items?.forEach {
                        Video.getOrCreate(realm, it.id).update(it)
                    }
                }
            }
        }
    }

    fun onVideoClick(video: Video) {
        view.navigateToVideo(video)
    }

    interface View {
        fun showError(@StringRes message: Int)
        fun navigateToVideo(video: Video)
    }
}