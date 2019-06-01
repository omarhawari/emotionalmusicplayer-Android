package com.eltrio.emotionalmusicplayer.my_classes

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

abstract class MyFragment : Fragment() {

    private fun doAll() {
        setupViews()
        setClickListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doAll()
    }

    abstract fun setupViews()

    abstract fun setClickListeners()

}
