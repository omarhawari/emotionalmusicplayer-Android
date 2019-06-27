package com.eltrio.emotionalmusicplayer.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import com.eltrio.emotionalmusicplayer.R
import com.eltrio.emotionalmusicplayer.fragments.SongsListFragment
import com.eltrio.emotionalmusicplayer.helpers.Data
import com.eltrio.emotionalmusicplayer.helpers.Functions
import com.eltrio.emotionalmusicplayer.my_classes.MyActivity
import com.eltrio.emotionalmusicplayer.my_classes.MyFragment
import com.eltrio.emotionalmusicplayer.my_classes.MyPagerAdapter
import com.eltrio.emotionalmusicplayer.volley.IVolleyRequest
import com.eltrio.emotionalmusicplayer.volley.VolleyRequest
import edu.cmu.pocketsphinx.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_main_content.*
import org.json.JSONArray
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : MyActivity() {

    private val SPEECH_INTENT = 1
    private val STORAGE_PERMESSIONS_REQUEST = 2

    private var recognizer: SpeechRecognizer? = null

    private val KWS_SEARCH = "hey emo"
    private val KEYPHRASE = "oh mighty computer"
    private val MENU_SEARCH = "menu"

    private val fragments = ArrayList<MyFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doAll()
        getMusic()
    }

    override fun setupViews() {
        song_list_view_pager.adapter = MyPagerAdapter(supportFragmentManager, fragments.apply {
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

        fab.setOnClickListener {
            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            if (speechIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(speechIntent, SPEECH_INTENT)
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }

        getMusic()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SPEECH_INTENT -> {
                if (resultCode == RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val speech = result[0]
                    val json = JSONObject()
                    json.put("speech", speech)
                    VolleyRequest.getInstance()
                        .post("http://192.168.1.7:3000/api/speechemotion", HashMap<String, String>().apply {
                            put("Dummy", "Data")
                        },
                            json.toString().toByteArray(), object : IVolleyRequest {
                                override fun onSuccess(response: JSONObject?) {
                                    Log.e("Response", response!!.toString())
                                }

                                override fun onFail(error: VolleyError?) {
                                    Log.e("Response", error!!.message + "")
                                }
                            })
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMESSIONS_REQUEST -> {
                if (EasyPermissions.hasPermissions(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    getMusic()
                }
            }
        }
    }

    private fun getMusic() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {

            Functions.getMusic(this)
            fragments.forEach {
                (it as SongsListFragment).onMusicLoaded()
            }
            val songs = JSONObject()
            Data.songs.forEach { it ->
                val artist = it.artist
                songs.put(artist, JSONArray())
                Data.songs.forEach {
                    if (it.artist == artist) {
                        (songs[artist] as JSONArray).put(it.title)
                    }
                }
            }
            VolleyRequest.getInstance().post("http://192.168.1.7:3000/api/lyricsemotion", HashMap(),
                songs.toString().toByteArray(), object : IVolleyRequest {
                    override fun onSuccess(response: JSONObject?) {

                    }

                    override fun onFail(error: VolleyError?) {

                    }
                })
        } else {
            EasyPermissions.requestPermissions(
                this, "", STORAGE_PERMESSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

}