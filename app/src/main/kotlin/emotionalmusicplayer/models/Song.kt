package emotionalmusicplayer.models

import android.net.Uri
import emotionalmusicplayer.helpers.Data
import org.json.JSONObject

class Song(val id: Long, val title: String, val artist: String, val lyrics: String = "", val uri: Uri,
           val emotions: JSONObject = JSONObject()) {
    override fun toString(): String {
        //lyrics='$lyrics',
        return "Song(id=$id, title='$title', artist='$artist,  uri=$uri\n, emotions=$emotions)\n"
    }
}