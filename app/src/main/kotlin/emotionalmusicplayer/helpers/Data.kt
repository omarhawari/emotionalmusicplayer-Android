package emotionalmusicplayer.helpers

import android.net.Uri
import emotionalmusicplayer.models.Song

object Data {

    enum class Emotions {
        EXCITED,
        HAPPY,
        NEUTRAL,
        SAD,
        STRESSED
    }

    val emotions = arrayOf("excited", "happy", "neutral", "sad", "stressed")

    val songs = ArrayList<Song>()
    val emotionalSongs = ArrayList<Song>()
    val suggestedSongs = ArrayList<Song>()
    val lyricLessSongs = ArrayList<Song>()
    val newSongs = ArrayList<Song>()

    fun getSongUriById(id: Int): Uri? {
        var uri: Uri? = null
        songs.forEach {
            if (it.id == id.toLong()) {
                uri = it.uri
            }
        }
        return uri
    }

    val BASE_URL = /*"192.168.43.138"*/  "http://192.168.1.7:3000"

}