package com.hackathon.youtubeview.model

import com.hackathon.youtubeview.api.YoutubeService
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Video : RealmObject() {
    @PrimaryKey
    var id: String? = null

    lateinit var title: String
    lateinit var description: String
    lateinit var date: Date
             var thumbnail: String? = null

    fun update(data: YoutubeService.YTItem): Video {
        val snippet = data.snippet ?: return this

        title = snippet.title
        description = snippet.description
        date = snippet.publishedAt
        thumbnail = snippet.thumbnails.get("medium")?.url ?: snippet.thumbnails.get("default")?.url
        return this
    }

    companion object {
        fun getOrCreate(realm: Realm, id: String): Video {
            var user = realm.where(Video::class.java).equalTo("id", id).findFirst()
            if (user == null) {
                user = realm.createObject(Video::class.java, id)
            }

            return user!!
        }
    }
}