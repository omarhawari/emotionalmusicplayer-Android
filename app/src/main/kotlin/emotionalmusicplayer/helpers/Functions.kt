package emotionalmusicplayer.helpers

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import emotionalmusicplayer.Database
import emotionalmusicplayer.models.Song
import org.json.JSONObject
import kotlin.math.absoluteValue

object Functions {

    fun getMusic(context: Context): ArrayList<Song>? {
        val musicResolver = context.contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

            var songList = ArrayList<Song>()

            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId)

                songList.add(Song(thisId, thisTitle, thisArtist, uri = uri))
            } while (musicCursor.moveToNext())

            songList = songList.filter {
                it.artist != "<unknown>"
            } as ArrayList<Song>
            val list = ArrayList<Song>().apply {
                addAll(songList)
            }
            Data.songs.clear()
            Data.songs.addAll(list)

            Data.emotionalSongs.clear()
            Data.emotionalSongs.addAll(Database.getSongs())

            return Data.emotionalSongs
        }
        return null
    }

    fun getSongsByEmotion(emotion: String): ArrayList<Song> {
        val musicByEmotion = ArrayList<Song>()

        Data.emotionalSongs.forEach { it ->
            val emotionsJSON = it.emotions
            val emotionVal = (emotionsJSON[emotion] as Double).toFloat()
            var isDominant = true
            emotionsJSON.keys().forEach {
                if (emotion != it && emotionVal < (emotionsJSON[it] as Double).toFloat()) {
                    isDominant = false
                }
            }
            if (isDominant) {
                musicByEmotion.add(it)
            }
        }

        return ArrayList<Song>().apply {
            Log.e(emotion, size.toString())
            addAll(musicByEmotion.sortedWith(compareBy { it.emotions[emotion] as Double }))
            reverse()
        }

    }

    fun getSongsByEmotionsJSON(emotionsJSON: JSONObject): ArrayList<Song> {
        val songs = ArrayList<Song>()

        val values = arrayListOf<Float>().apply {
            emotionsJSON.keys().forEach {
                add((emotionsJSON[it] as Double).toFloat())
            }
        }

        var maxIndex = 0
        var max = -1F
        values.forEachIndexed { index, value ->
            if (max < value) {
                maxIndex = index
                max = value
            }
        }

        return Data.suggestedSongs.apply {
            clear()
            addAll(getSongsByEmotion(Data.emotions[maxIndex]).sortedWith(compareBy {
                ((it.emotions[Data.emotions[maxIndex]] as Double)).absoluteValue.apply {
                    Log.e("differ", this.toString())
                }
            }))
        }
    }

}