package com.hackathon.youtubeview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hackathon.youtubeview.api.YoutubeService
import com.hackathon.youtubeview.api.enqueue
import com.hackathon.youtubeview.model.Video
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        syncVideos()

        video_list.apply {
            setHasFixedSize(true)
            adapter = VideoAdapter(Realm.getDefaultInstance().where(Video::class.java).findAllAsync())
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun syncVideos() {
        val youtube = YoutubeService.create(BuildConfig.API_KEY)
        val channel = BuildConfig.CHANNEL

        // We need to first get the "uploads" playlist ID from a channel ID.
        // Then we can get a list of videos in that playlist
        youtube.getChannelInfo(channel).enqueue { result ->
            val playlists = result.getOrNull()?.items?.getOrNull(0)?.contentDetails?.relatedPlaylists
            val uploadsPlaylistId = playlists?.get("uploads")
            if (uploadsPlaylistId == null) {
                Toast.makeText(this@MainActivity, R.string.unable_uploads, Toast.LENGTH_LONG).show()
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
