package com.eltrio.emotionalmusicplayer.activities

import android.os.Bundle
import android.util.Log
import com.android.volley.VolleyError
import com.eltrio.emotionalmusicplayer.R
import com.eltrio.emotionalmusicplayer.fragments.SongsListFragment
import com.eltrio.emotionalmusicplayer.my_classes.MyActivity
import com.eltrio.emotionalmusicplayer.my_classes.MyFragment
import com.eltrio.emotionalmusicplayer.my_classes.MyPagerAdapter
import com.eltrio.emotionalmusicplayer.volley.IVolleyRequest
import com.eltrio.emotionalmusicplayer.volley.VolleyRequest
import kotlinx.android.synthetic.main.activity_main_content.*
import org.json.JSONObject

class MainActivity : MyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doAll()

        VolleyRequest.getInstance().post("http://192.168.1.7:80/api/", HashMap<String, String>().apply {
            this["age"] = "212"
        }, object : IVolleyRequest {
            override fun onSuccess(response: JSONObject?) {
                Log.e("Response", response.toString())
            }

            override fun onFail(error: VolleyError?) {
                Log.e("Error", error.toString())
            }



        })

    }

    override fun setupViews() {
        song_list_view_pager.adapter = MyPagerAdapter(supportFragmentManager, ArrayList<MyFragment>().apply {
            add(SongsListFragment())
            add(SongsListFragment())
            add(SongsListFragment())
            add(SongsListFragment())
        }, ArrayList<String>().apply {
            add("Happy")
            add("Sad")
            add("Angry")
            add("Happy")
        })

        song_lists_tabs.setupWithViewPager(song_list_view_pager, false)
    }

    override fun setClickListeners() {
        super.setClickListeners()
    }
}