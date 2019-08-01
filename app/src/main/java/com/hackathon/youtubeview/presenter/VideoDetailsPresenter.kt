package com.hackathon.youtubeview.presenter

import com.hackathon.youtubeview.model.Video
import io.realm.Realm

class VideoDetailsPresenter(private val view: View) {
    private var video: Video? = null

    fun setup(id: String) {
        video = Realm.getDefaultInstance().where(Video::class.java).equalTo("id", id).findFirst()
        view.setDetails(video!!)
    }

    fun onPlayClick() {
        if (video != null) {
            view.openURL("https://www.youtube.com/watch?v=${video!!.id}")
        }
    }

    interface View {
        fun setDetails(video: Video)
        fun openURL(url: String)
    }
}