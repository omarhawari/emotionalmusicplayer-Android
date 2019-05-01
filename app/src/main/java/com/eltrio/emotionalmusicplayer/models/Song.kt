package com.eltrio.emotionalmusicplayer.models

class Song(val id: Long, val title: String, val artist: String) {
    override fun toString(): String {
        return "Song(id=$id, title='$title', artist='$artist')"
    }
}