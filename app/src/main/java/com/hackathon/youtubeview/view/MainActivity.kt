package com.hackathon.youtubeview.view

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hackathon.youtubeview.R
import com.hackathon.youtubeview.model.Video
import com.hackathon.youtubeview.presenter.MainPresenter
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainPresenter.View {
    private val presenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.syncVideos()

        video_list.apply {
            setHasFixedSize(true)
            adapter =
                VideoAdapter(Realm.getDefaultInstance().where(Video::class.java).findAllAsync())
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun showError(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    class VideoAdapter(data : RealmResults<Video>) : RealmRecyclerViewAdapter<Video, VideoAdapter.ViewHolder>(data, true, true) {
        class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
            val title: TextView = root.findViewById(R.id.title)
            val date: TextView = root.findViewById(R.id.date)
            val thumbnail: ImageView = root.findViewById(R.id.thumbnail)
        }

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.video, parent, false)
            return ViewHolder(rootView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)

            if (item != null) {
                holder.title.text = item.title
            }
        }
    }
}
