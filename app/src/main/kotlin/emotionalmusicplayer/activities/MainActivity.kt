package emotionalmusicplayer.activities

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import com.android.volley.VolleyError
import com.eltrio.emotionalmusicplayer.R
import emotionalmusicplayer.Database
import emotionalmusicplayer.fragments.EmotionalEqualizerFragment
import emotionalmusicplayer.fragments.SongsListFragment
import emotionalmusicplayer.fragments.SuggestedPlayListFragment
import emotionalmusicplayer.helpers.Data
import emotionalmusicplayer.helpers.Data.BASE_URL
import emotionalmusicplayer.helpers.Functions
import emotionalmusicplayer.models.Song
import emotionalmusicplayer.my_classes.MyActivity
import emotionalmusicplayer.my_classes.MyFragment
import emotionalmusicplayer.my_classes.MyPagerAdapter
import emotionalmusicplayer.volley.IVolleyRequest
import emotionalmusicplayer.volley.VolleyRequest
import kotlinx.android.synthetic.main.activity_main_appbar.*
import kotlinx.android.synthetic.main.activity_main_content.*
import org.json.JSONArray
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : MyActivity() {

    val mediaPlayer = MediaPlayer()

    private val SPEECH_INTENT = 1
    private val STORAGE_PERMESSIONS_REQUEST = 2

    private val fragments = ArrayList<MyFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Database.init(this)
        doAll()
        getMusic()
    }

    override fun setupViews() {
        song_list_view_pager.adapter = MyPagerAdapter(supportFragmentManager, fragments.apply {
            add(SongsListFragment().apply { arguments = Bundle().apply { putString("emotion", "excited") } })
            add(SongsListFragment().apply { arguments = Bundle().apply { putString("emotion", "happy") } })
            add(SongsListFragment().apply { arguments = Bundle().apply { putString("emotion", "neutral") } })
            add(SongsListFragment().apply { arguments = Bundle().apply { putString("emotion", "sad") } })
            add(SongsListFragment().apply { arguments = Bundle().apply { putString("emotion", "stressed") } })
        }, ArrayList<String>().apply {
            Data.Emotions.values().forEach {
                add(it.toString())
            }
        })

        song_lists_tabs.setupWithViewPager(song_list_view_pager, false)

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                seekbar.progress = mediaPlayer.currentPosition
            }
        }, 0, 1000)


    }

    override fun setClickListeners() {
        play_pause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        fab_speech.setOnClickListener {
            fab_menu.collapse()
            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            if (speechIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(speechIntent, SPEECH_INTENT)
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }

        fab_calibrator.setOnClickListener {
            fab_menu.collapse()
            val dialogFragment = EmotionalEqualizerFragment()
            dialogFragment.show(supportFragmentManager, EmotionalEqualizerFragment.TAG)
        }

    }

    fun playSong(song: Song) {
        mediaPlayer.reset()

        try {
            mediaPlayer.setDataSource(this, song.uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer.start()

        seekbar.max = mediaPlayer.duration
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
                        .post("http://$BASE_URL:3000/api/speechemotion", null,
                              json.toString().toByteArray(), object : IVolleyRequest {
                                override fun onSuccess(response: JSONObject?) {
                                    Functions.getSongsByEmotionsJSON(response!!.getJSONObject("emotion"))
                                    val suggestedPlayListFragment = SuggestedPlayListFragment()
                                    suggestedPlayListFragment.arguments = Bundle().apply {
                                        putString("emotion", response.getString("emotion"))
                                    }
                                    suggestedPlayListFragment.show(supportFragmentManager,
                                                                   SuggestedPlayListFragment.TAG)
                                }

                                override fun onFail(error: VolleyError?) {
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
                if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                   Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    getMusic()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.refresh -> {
                getMusic()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMusic() {
        if (EasyPermissions.hasPermissions(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Functions.getMusic(this)

            fragments.forEach {
                (it as SongsListFragment).onMusicLoaded()
            }
            return
            val songs = JSONObject()
            Data.songs.forEach { it ->
                val artist = it.artist
                songs.put(artist, JSONArray())
                Data.songs.forEach {
                    if (it.artist == artist)
                        (songs[artist] as JSONArray).put(JSONObject().apply {
                            put("id", it.id.toString())
                            put("title", it.title)
                        })
                }
            }

            Log.e("Sent", songs.toString())

            VolleyRequest.getInstance().post("http://$BASE_URL:3000/api/lyricsemotion", HashMap(),
                songs.toString().toByteArray(), object : IVolleyRequest {
                    override fun onSuccess(response: JSONObject?) {
                        val jsonResponse = response!!
                        Database.addSongs(response)

                        Log.e("Songs", Arrays.toString(Database.getSongs().toTypedArray()))

//                        jsonResponse.keys().forEach {
//                            Log.e(it, jsonResponse[it].toString())
//                        }
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