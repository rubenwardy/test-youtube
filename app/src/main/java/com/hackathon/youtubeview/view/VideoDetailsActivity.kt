package com.hackathon.youtubeview.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.hackathon.youtubeview.R
import com.hackathon.youtubeview.model.Video
import com.hackathon.youtubeview.presenter.VideoDetailsPresenter
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.content_video_details.*


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
        supportActionBar!!.title = null

        findViewById<TextView>(R.id.title).text = video.title
        val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
        findViewById<TextView>(R.id.subtitle).text = dateFormat.format(video.date)
        findViewById<TextView>(R.id.description).text = video.description

        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                toolbar_layout.tag = null
                toolbar_layout.background = BitmapDrawable(resources, bitmap)
                scroll_view.requestFocus()
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {}

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }

        Picasso.get()
            .load(video.image)
            .into(target)
    }
}
