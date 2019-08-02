package com.hackathon.youtubeview.model

import com.hackathon.youtubeview.api.YoutubeService
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Comment : RealmObject() {
    @PrimaryKey
    var id: String? = null

    lateinit var author: String
    lateinit var authorImageURL: String
    lateinit var textDisplay: String

    fun update(data: YoutubeService.YTItem): Comment {
        val snippet = data.snippet?.topLevelComment?.snippet ?: return this

        author = snippet.authorDisplayName!!
        authorImageURL = snippet.authorProfileImageUrl!!
        return this
    }

    companion object {
        fun getOrCreate(realm: Realm, id: String): Comment {
            var comment = realm.where(Comment::class.java).equalTo("id", id).findFirst()
            if (comment == null) {
                comment = realm.createObject(Comment::class.java, id)
            }

            return comment!!
        }
    }

}