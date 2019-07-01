package emotionalmusicplayer.helpers

import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import emotionalmusicplayer.Database
import emotionalmusicplayer.fragments.SuggestedPlayListFragment
import emotionalmusicplayer.models.Song
import emotionalmusicplayer.my_classes.MyActivity
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

//            Database.cleanNoneLyricedSongs()

            Data.songs.clear()
            Data.songs.addAll(list)

            Data.emotionalSongs.clear()
            Database.getEmotionalSongs()

            Data.lyricLessSongs.clear()
            Data.lyricLessSongs.addAll(Database.getNoneLyricedSongs())
            Database.addNonLyricedSongs(Data.lyricLessSongs)

            getNewSongs()
            return Data.emotionalSongs
        }
        return null
    }

    private fun getNewSongs() {
        Data.newSongs.clear()
        Data.newSongs.addAll(Data.songs)
        Data.emotionalSongs.forEach {
            val song = findSongById(it.id, Data.newSongs)
            if (song != null) {
                Data.newSongs.remove(song)
            }
        }
        Data.newSongs.removeAll(Data.lyricLessSongs)
    }

    fun deleteFromFirstTheSecond(first: ArrayList<Song>, second: ArrayList<Song>) {
        second.forEach {
            val song = findSongById(it.id, first)
            if (song != null) {
                first.remove(song)
            }
        }
    }

    fun findSongById(id: Long, songList: ArrayList<Song>): Song? {
        for (i in 0 until songList.size) if (songList[i].id == id) return songList[i]
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
            addAll(musicByEmotion.sortedWith(compareBy { it.emotions[emotion] as Double }))
            reverse()
        }

    }

    private fun getSongsByEmotionsJSON(emotionsJSON: JSONObject): ArrayList<Song> {
        val values = arrayListOf<Float>().apply {
            emotionsJSON.keys().forEach {
                add((emotionsJSON.getString(it)).toFloat())
            }
        }

        var emotionIndex = 0
        var max = -1F
        values.forEachIndexed { index, value ->
            if (max < value) {
                emotionIndex = index
                max = value
            }
        }

        return Data.suggestedSongs.apply {
            clear()
            addAll(Data.emotionalSongs.sortedWith(compareBy {
                //                var difference = 0.0
//                it.emotions.keys().forEach {
//                    Log.e("Key1", it.toString())
//                }
//                Log.e("Emotions", emotionsJSON.toString())
//                Log.e("Emotions2", it.emotions.toString())
//                Data.emotions.forEach { emotion ->
//                    difference += sqrt(it.emotions.getDouble(emotion) - emotionsJSON.getDouble(emotion))
//                }
//                difference
                (it.emotions[Data.emotions[emotionIndex]] as Double - emotionsJSON.getDouble(Data.emotions[emotionIndex]))
                    .absoluteValue
            }))
        }
    }

    fun showPredictedList(activity: MyActivity, emotionsJSON: JSONObject) {
        getSongsByEmotionsJSON(emotionsJSON)
        val suggestedPlayListFragment = SuggestedPlayListFragment()
        suggestedPlayListFragment.arguments = Bundle().apply {
            putString("emotion", emotionsJSON.toString())
        }
        suggestedPlayListFragment.show(activity.supportFragmentManager,
                                       SuggestedPlayListFragment.TAG)

    }

    fun secondsToDuration(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours == 0) {
            "${addZeros(minutes)}:${addZeros(seconds)}"
        } else {
            "${addZeros(hours)}:${addZeros(minutes)}:${addZeros(seconds)}"
        }
    }

    private fun addZeros(value: Int): String {
        return if (value < 10) "0$value" else "$value"
    }
}