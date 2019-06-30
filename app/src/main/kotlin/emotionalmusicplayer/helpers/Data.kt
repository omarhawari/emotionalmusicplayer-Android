package emotionalmusicplayer.helpers

import android.net.Uri
import emotionalmusicplayer.models.Song

object Data {

    enum class Emotions {
        HAPPY,
        SAD,
        STRESSED,
        EXCITED,
        NEUTRAL
    }

    val songs = ArrayList<Song>()

    fun getSongUriById(id: Int): Uri? {
        var uri: Uri? = null
        songs.forEach {
            if (it.id == id.toLong()) {
                uri = it.uri
            }
        }
        return uri
    }

    val BASE_URL = /*"192.168.43.138"*/  "192.168.1.7"

}