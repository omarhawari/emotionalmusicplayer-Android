package com.eltrio.emotionalmusicplayer.app

import android.app.Application
import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.*

class MyApp : Application() {

    private var mRequestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MyApp? = null
            private set
    }
}
