package emotionalmusicplayer.app

import android.app.Application
import com.android.volley.RequestQueue

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MyApp? = null
            private set
    }
}
