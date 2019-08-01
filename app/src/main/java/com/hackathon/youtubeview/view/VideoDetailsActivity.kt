package com.hackathon.youtubeview.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hackathon.youtubeview.R
import com.hackathon.youtubeview.model.Video
import com.hackathon.youtubeview.presenter.VideoDetailsPresenter
import kotlinx.android.synthetic.main.activity_video_details.*

class VideoDetailsActivity : AppCompatActivity(), VideoDetailsPresenter.View {
    private val presenter = VideoDetailsPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener {
            presenter.onPlayClick()
        }

        presenter.setup(intent.getStringExtra("id"))
    }

    override fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun setDetails(video: Video) {
        supportActionBar!!.title = video.title
    }
}
