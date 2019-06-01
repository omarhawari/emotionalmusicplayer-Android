package com.eltrio.emotionalmusicplayer.my_classes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class MyActivity : AppCompatActivity() {

    fun doAll() {
        setupViews()
        setClickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    open fun setupViews() {}

    open fun setClickListeners() {}

}