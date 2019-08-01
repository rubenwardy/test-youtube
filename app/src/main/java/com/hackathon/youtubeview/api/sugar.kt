package com.hackathon.youtubeview.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.enqueue(func: (Result<T>) -> Unit) {
    enqueue(object: Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val res = response.body()
            if (res == null) {
                Log.e("RetrofitSugar", response.toString())
                func(Result.failure(Exception(response.toString())))
            } else {
                func(Result.success(response.body()!!))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e("RetrofitSugar", t.toString())
            func(Result.failure(t))
        }
    })
}