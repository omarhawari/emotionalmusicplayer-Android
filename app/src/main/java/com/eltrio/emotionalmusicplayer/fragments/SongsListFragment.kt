package com.eltrio.emotionalmusicplayer.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eltrio.emotionalmusicplayer.R
import com.eltrio.emotionalmusicplayer.adapters.SongListAdapter
import com.eltrio.emotionalmusicplayer.helpers.Data
import com.eltrio.emotionalmusicplayer.models.Song
import com.eltrio.emotionalmusicplayer.my_classes.MyAdapter
import com.eltrio.emotionalmusicplayer.my_classes.MyFragment
import kotlinx.android.synthetic.main.songs_list_fragment.*

class SongsListFragment : MyFragment() {

    var songsAdapter: SongListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.songs_list_fragment, container, false)
    }

    override fun setupViews() {
        songsAdapter = SongListAdapter(context!!, Data.songs, object : MyAdapter.OnClickListener<Song> {
            override fun onClick(holder: RecyclerView.ViewHolder, position: Int, item: Song) {

            }
        })
        song_list_rv.adapter = songsAdapter
        song_list_rv.layoutManager = LinearLayoutManager(context)
    }

    override fun setClickListeners() {

    }

    fun onMusicLoaded() {
        songsAdapter?.updateData(Data.songs)
    }
}