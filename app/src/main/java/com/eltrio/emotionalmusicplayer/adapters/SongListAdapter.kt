package com.eltrio.emotionalmusicplayer.adapters

import android.content.Context
import android.view.View
import com.eltrio.emotionalmusicplayer.R
import com.eltrio.emotionalmusicplayer.models.Song
import com.eltrio.emotionalmusicplayer.my_classes.MyAdapter
import com.eltrio.emotionalmusicplayer.my_classes.MyViewHolder
import kotlinx.android.synthetic.main.rv_song.view.*

class SongListAdapter(context: Context, data: ArrayList<Song>) :
    MyAdapter<SongListAdapter.Holder, Song>(context, data, R.layout.rv_song) {

    override fun onBindViewHolderItem(holder: Holder, item: Song) {
        holder.songTitle.text = item.title
        holder.artist.text = item.artist
    }

    class Holder(rootView: View) : MyViewHolder(rootView) {
        val play = rootView.play
        val songTitle = rootView.song_title
        val artist = rootView.song_artist
    }

}