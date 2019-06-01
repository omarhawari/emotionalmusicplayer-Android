package com.eltrio.emotionalmusicplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eltrio.emotionalmusicplayer.R
import com.eltrio.emotionalmusicplayer.my_classes.MyFragment

class SongsListFragment : MyFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.songs_list_fragment, container, false)
    }

    override fun setupViews() {

    }

    override fun setClickListeners() {

    }
}