package emotionalmusicplayer

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import emotionalmusicplayer.helpers.Data
import emotionalmusicplayer.models.Song
import org.json.JSONArray
import org.json.JSONObject

object Database {

    private const val DATABASE_NAME = "EMOTIONAL_MUSIC_PLAYER"

    private const val TABLE_SONGS = "SONGS"
    private const val COL_ID = "id"
    private const val COL_TITLE = "title"
    private const val COL_ARTIST = "artist"
    private const val COL_LYRICS = "lyrics"
    private const val COL_EMOTION = "emotion"

    private const val TABLE_NON_LYRICS = "NON_LYRICS"


    lateinit var database: SQLiteDatabase

    fun init(context: Context) {
        database = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null)
    }

    fun addSongs(songsJSON: JSONObject) {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS $TABLE_SONGS ($COL_ID INTEGER PRIMARY KEY, $COL_TITLE VARCHAR, " +
                        "$COL_ARTIST VARCHAR, $COL_LYRICS VARCHAR," +
                        "$COL_EMOTION VARCHAR)")

        songsJSON.keys().forEach { artist ->
            val array = songsJSON[artist] as JSONArray
            for (i in 0 until array.length()) {
                val song = array[i] as JSONObject
                val contentValues = ContentValues()
                contentValues.put(COL_ID, (song[COL_ID] as String).toInt())
                contentValues.put(COL_TITLE, song[COL_TITLE] as String)
                contentValues.put(COL_ARTIST, artist)
                val re = Regex("[\n\t]")
                val lyrics = re.replace((song[COL_LYRICS] as String), " ") // works

                contentValues.put(COL_LYRICS, lyrics)
                contentValues.put(COL_EMOTION, (song[COL_EMOTION] as JSONObject).toString())

                val query = database.rawQuery("SELECT * FROM $TABLE_SONGS WHERE $COL_ID = ${song[COL_ID] as String}", null)
                query.moveToFirst()
                if (query.isAfterLast) {
                    database.insert(TABLE_SONGS, null, contentValues)
                } else {
                    database.update(TABLE_SONGS,contentValues ,"$COL_ID = ${song[COL_ID] as String}", null)
                }
                query.close()
            }
        }
    }

    fun getEmotionalSongs() {
        val query = database.rawQuery("SELECT * FROM $TABLE_SONGS", null)

        val col_id = query.getColumnIndex(COL_ID)
        val col_title = query.getColumnIndex(COL_TITLE)
        val col_artist = query.getColumnIndex(COL_ARTIST)
        val col_lyrics = query.getColumnIndex(COL_LYRICS)
        val col_emotion = query.getColumnIndex(COL_EMOTION)
        query.moveToFirst()

        while (!query.isAfterLast) {
            Data.emotionalSongs.add(Song(
                    query.getInt(col_id).toLong(),
                    query.getString(col_title),
                    query.getString(col_artist),
                    query.getString(col_lyrics),
                    Data.getSongUriById(query.getInt(col_id))!!,
                    JSONObject(query.getString(col_emotion))
            ))
            query.moveToNext()
        }

        query.close()
    }

    fun addNonLyricedSongs(songs: ArrayList<Song>) {

        database.execSQL(
                "CREATE TABLE IF NOT EXISTS $TABLE_NON_LYRICS ($COL_ID INTEGER PRIMARY KEY)")
        songs.forEach {
            val contentValues = ContentValues().apply {
                put(COL_ID, it.id)
            }

            val query = database.rawQuery("SELECT * FROM $TABLE_NON_LYRICS WHERE $COL_ID = ${it.id}",
                                          null)
            query.moveToFirst()
            if (query.isAfterLast) {
                database.insert(TABLE_NON_LYRICS, null, contentValues)
            } else {
                database.update(TABLE_NON_LYRICS, contentValues, "$COL_ID = ${it.id}", null)
            }

            query.close()
        }
    }

    fun getNoneLyricedSongs(): ArrayList<Song> {

        val noneLyricedSongs = ArrayList<Song>()
        Data.songs.forEach {
            val query = database.rawQuery("SELECT * FROM $TABLE_NON_LYRICS WHERE $COL_ID = ${it.id}", null)
            query.moveToFirst()
            if (!query.isAfterLast) {
                noneLyricedSongs.add(it)
            }

            query.close()
        }

        return noneLyricedSongs
    }

    fun cleanNoneLyricedSongs() {
        val query = database.rawQuery("SELECT * FROM $TABLE_NON_LYRICS", null)
        query.moveToFirst()
        while (!query.isAfterLast) {
            var id = 0
            val query2 = database.rawQuery("SELECT * FROM $TABLE_SONGS WHERE $COL_ID = " +
                                                   "${query.getInt(query.getColumnIndex(COL_ID)).apply {
                                                       id = this
                                                   }}", null)
            query2.moveToFirst()
            if (!query.isAfterLast) {
                database.delete(TABLE_NON_LYRICS, "$COL_ID = $id", null)
            }
            query2.close()
            query.moveToNext()
        }
        query.close()
    }

    fun printNoneLyricedSongs() {
        val query = database.rawQuery("SELECT * FROM $TABLE_SONGS", null)
        query.moveToFirst()
        while (!query.isAfterLast) {
            query.moveToNext()
        }
    }

}