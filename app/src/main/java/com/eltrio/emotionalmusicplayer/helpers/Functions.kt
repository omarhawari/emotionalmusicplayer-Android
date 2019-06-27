package com.eltrio.emotionalmusicplayer.helpers

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import com.eltrio.emotionalmusicplayer.models.Song


object Functions {

    fun getMusic(context: Context): ArrayList<Song>? {
        val musicResolver = context.contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)

            var songList = ArrayList<Song>()

            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                songList.add(Song(thisId, thisTitle, thisArtist))
            } while (musicCursor.moveToNext())
            songList = songList.filter {
                it.artist != "<unknown>"
            } as ArrayList<Song>
            val list = ArrayList<Song>().apply {
                addAll(songList.subList(0, 10))
            }
            Log.e("Count", songList.size.toString())
            return list.apply {
                Data.songs.clear()
                Data.songs.addAll(this)
            }
        }
        return null
    }

}