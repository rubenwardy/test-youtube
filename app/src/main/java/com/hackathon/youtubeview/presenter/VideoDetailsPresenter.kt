package com.hackathon.youtubeview.presenter

import com.hackathon.youtubeview.BuildConfig
import com.hackathon.youtubeview.api.YoutubeService
import com.hackathon.youtubeview.api.enqueue
import com.hackathon.youtubeview.model.Comment
import com.hackathon.youtubeview.model.Video
import io.realm.Realm

class VideoDetailsPresenter(private val view: View) {
    private var video: Video? = null

    fun setup(id: String) {
        video = Realm.getDefaultInstance().where(Video::class.java).equalTo("id", id).findFirst()
        view.setDetails(video!!)

        if (video!!.duration == null) {
            fetchDuration(video!!)
        }

        fetchComments(video!!.id!!)
    }

    private fun fetchDuration(video: Video) {
        YoutubeService.create(BuildConfig.API_KEY).getVideo(video.id!!).enqueue {
            val duration: String = it.getOrNull()?.items?.get(0)?.contentDetails?.duration
                ?: return@enqueue

            Realm.getDefaultInstance().executeTransaction {
                video.duration = duration
            }

            view.setDetails(video)
        }
    }

    private fun fetchComments(videoID: String) {
        YoutubeService.create(BuildConfig.API_KEY).getComments(videoID).enqueue {
            val comments = it.getOrNull()?.items
                ?: return@enqueue

            Realm.getDefaultInstance().executeTransaction { realm ->
                video?.comments?.clear()
                comments.forEach { item ->
                    val comment = Comment.getOrCreate(realm, item.snippet!!.topLevelComment!!.id)
                    comment.update(item)
                    video?.comments?.add(comment)
                }
            }
        }
    }

    fun onPlayClick() {
        if (video != null) {
            view.openURL(video!!.url)
        }
    }

    interface View {
        fun setDetails(video: Video)
        fun openURL(url: String)
    }
}