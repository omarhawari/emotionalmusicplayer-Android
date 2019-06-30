package emotionalmusicplayer.helpers

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import emotionalmusicplayer.models.Song
import org.json.JSONObject

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
            return list.apply {
                Data.songs.clear()
                Data.songs.addAll(this)
            }
        }
        return null
    }

}