package com.hackathon.youtubeview.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hackathon.youtubeview.R
import com.hackathon.youtubeview.model.Comment
import com.hackathon.youtubeview.model.Video
import com.hackathon.youtubeview.presenter.VideoDetailsPresenter
import com.hackathon.youtubeview.util.Duration
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.content_video_details.*
import kotlin.math.max


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

        val duration = if (video.duration == null) "" else Duration.parse(video.duration!!).toString() + " • "
        val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
        findViewById<TextView>(R.id.subtitle).text = duration + dateFormat.format(video.date)
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

    override fun showComments(comments: RealmResults<Comment>) {
        comment_list.apply {
            setHasFixedSize(true)
            adapter = CommentAdapter(comments)
            layoutManager = LinearLayoutManager(context)
        }
    }

    class CommentAdapter(data : RealmResults<Comment>) : RealmRecyclerViewAdapter<Comment, CommentAdapter.ViewHolder>(data, true, true) {
        class ViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
            val author: TextView = root.findViewById(R.id.author)
            val text: TextView = root.findViewById(R.id.text)
            val thumbnail: ImageView = root.findViewById(R.id.thumbnail)
        }

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.comment, parent, false)
            return ViewHolder(rootView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            if (item != null) {
                holder.author.text = item.author
                holder.text.text = item.textDisplay

                val callback = object : Callback {
                    override fun onSuccess() {
                        val imageBitmap = (holder.thumbnail.drawable as BitmapDrawable).bitmap
                        val imageDrawable = RoundedBitmapDrawableFactory.create(holder.root.context.resources, imageBitmap)
                        imageDrawable.isCircular = true
                        imageDrawable.cornerRadius = max(imageBitmap.width, imageBitmap.height) / 2.0f
                        holder.thumbnail.setImageDrawable(imageDrawable)
                    }

                    override fun onError(e: Exception) {}
                }

                Picasso.get()
                    .load(item.authorImageURL)
                    .fit()
                    .placeholder(R.drawable.circle_placeholder)
                    .into(holder.thumbnail, callback)
            }
        }
    }
}
