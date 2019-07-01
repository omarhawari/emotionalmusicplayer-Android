package emotionalmusicplayer.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.VolleyError
import com.eltrio.emotionalmusicplayer.R
import emotionalmusicplayer.Database
import emotionalmusicplayer.fragments.EmotionalEqualizerFragment
import emotionalmusicplayer.fragments.SongsListFragment
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : MyActivity() {

    val mediaPlayer = MediaPlayer()

    private val SPEECH_INTENT = 1
    private val STORAGE_PERMESSIONS_REQUEST = 2
    private val CAMERA_INTENT = 3
    private val CAMERA_PERMISSIONS_REQUEST = 4

    private lateinit var imageUri: Uri

    private val fragments = ArrayList<MyFragment>()

    public var song: Song? = null
    private lateinit var songList: ArrayList<Song>

    private var repeatToggle = 0 // 0: No repeat, 1: Repeat all, 2: Repeat one
    private var shuffleToggle = 0 // 0: No shuffleToggle, 1: Shuffle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Database.init(this)
        doAll()
        getMusic()
        previous_song.isEnabled = false
        next_song.isEnabled = false
        play_pause.isEnabled = false
//        song_info.visibility = View.GONE
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
                runOnUiThread {
                    if (mediaPlayer.isPlaying)
                        progress.text = Functions.secondsToDuration(seekbar.progress / 1000)
                }
            }
        }, 0, 1000)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
    }

    override fun setClickListeners() {
        play_pause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                play_pause.setImageDrawable(getDrawable(R.drawable.play))
                mediaPlayer.pause()
            } else {
                play_pause.setImageDrawable(getDrawable(R.drawable.pause))
                mediaPlayer.start()
            }
            play_pause.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
        }

        next_song.setOnClickListener {
            playNext()
        }

        previous_song.setOnClickListener {
            playPrevious()
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

        fab_camera.setOnClickListener {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                takeImage()
            } else {
                EasyPermissions.requestPermissions(this, "", CAMERA_PERMISSIONS_REQUEST, Manifest.permission.CAMERA)
            }
        }

        repeat_toggle.setOnClickListener {
            repeatToggle++
            if (repeatToggle == 3) repeatToggle = 0
            when (repeatToggle) {
                0 -> {
                    repeat_toggle.setImageDrawable(getDrawable(R.drawable.repeat))
                    repeat_toggle.drawable.setTint(resources.getColor(android.R.color.darker_gray))
                }
                1 -> {
                    repeat_toggle.setImageDrawable(getDrawable(R.drawable.repeat))
                    repeat_toggle.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
                    shuffleToggle = 0
                    shuffle_toggle.drawable.setTint(resources.getColor(android.R.color.darker_gray))

                }
                2 -> {
                    repeat_toggle.setImageDrawable(getDrawable(R.drawable.repeat_one))
                    repeat_toggle.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
                    shuffleToggle = 0
                    shuffle_toggle.drawable.setTint(resources.getColor(android.R.color.darker_gray))
                }
            }
        }

        shuffle_toggle.setOnClickListener {
            shuffleToggle++
            if (shuffleToggle == 2) shuffleToggle = 0
            when (shuffleToggle) {
                0 -> {
                    shuffle_toggle.drawable.setTint(resources.getColor(android.R.color.darker_gray))
                    repeat_toggle.setImageDrawable(getDrawable(R.drawable.repeat))
                    repeat_toggle.drawable.setTint(resources.getColor(android.R.color.darker_gray))
                    repeatToggle = 0
                }
                1 -> {
                    shuffle_toggle.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
                    repeat_toggle.setImageDrawable(getDrawable(R.drawable.repeat))
                    repeat_toggle.drawable.setTint(resources.getColor(android.R.color.darker_gray))
                    repeatToggle = 0
                }
            }
        }

        mediaPlayer.setOnCompletionListener {
            if (shuffleToggle == 0) {
                when (repeatToggle) {
                    1 -> {
                        playNext()
                    }
                    2 -> {
                        playSong(song!!, songList)
                    }
                }
            } else if (repeatToggle == 0) {
                when (shuffleToggle) {
                    1 -> {
                        playShuffle()
                    }
                }
            }
        }

    }

    private fun takeImage() {
        Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                              FileProvider.getUriForFile(this, "com.eltrio.emotionalmusicplayer.fileProvider",
                                                         photo).apply { imageUri = this })

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())


        startActivityForResult(cameraIntent, CAMERA_INTENT)
    }

    private fun sendPhoto(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "")

        image.setImageBitmap(bitmap)

        VolleyRequest.getInstance()
            .post("$BASE_URL/api/imageemotion", null, JSONObject().apply {
                put("image", encoded)
            }.toString().toByteArray(), object : IVolleyRequest {
                override fun onSuccess(response: JSONObject?) {
                    Functions.showPredictedList(this@MainActivity, response!!.getJSONObject("emotion"))
                }

                override fun onFail(error: VolleyError?) {

                }
            })

    }


    fun playSong(song: Song, songList: ArrayList<Song>) {
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
        play_pause.setImageDrawable(getDrawable(R.drawable.pause))
        mediaPlayer.start()

        seekbar.max = mediaPlayer.duration

        this.song = song
        this.songList = songList
        fragments.forEach {
            (it as SongsListFragment).updateData()
        }
        total_duration.text = Functions.secondsToDuration(seekbar.max / 1000)
        activateButtons()


        val updateTextView = Runnable {
        }

        runOnUiThread(updateTextView)

    }

    private fun activateButtons() {
        song_info.visibility = View.VISIBLE
        previous_song.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
        next_song.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
        play_pause.drawable.setTint(resources.getColor(R.color.colorPrimaryDark))
        previous_song.isEnabled = true
        next_song.isEnabled = true
        play_pause.isEnabled = true
        title_song.text = song?.title
        artist.text = song?.artist
        title_song.isSelected = true
        artist.isSelected = true
    }

    private fun playNext() {
        var index = songList.indexOf(song)
        index++
        if (index == songList.size) index = 0
        song = songList[index]
        playSong(song!!, songList)
    }

    private fun playPrevious() {
        var index = songList.indexOf(song)
        index--
        if (index == -1) index = songList.size - 1
        song = songList[index]
        playSong(song!!, songList)
    }

    private fun playShuffle() {
        val index = (0 until songList.size).random()
        song = songList[index]
        playSong(song!!, songList)
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
                        .post("$BASE_URL/api/speechemotion", null,
                              json.toString().toByteArray(), object : IVolleyRequest {
                            override fun onSuccess(response: JSONObject?) {
                                Functions.showPredictedList(this@MainActivity,
                                                            response!!.getJSONObject("emotion"))
                            }

                            override fun onFail(error: VolleyError?) {
                            }
                        })
                }
            }
            CAMERA_INTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    contentResolver.notifyChange(imageUri, null)

                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        sendPhoto(bitmap)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

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
            CAMERA_PERMISSIONS_REQUEST  -> {
                if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                    takeImage()
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

            if (Data.newSongs.size > 0) {
                AlertDialog.Builder(this)
                    .setTitle("You have new unarchived songs")
                    .setMessage("Do you want to archive them?")
                    .setPositiveButton("Yes") { _, _ ->

                        val songs = JSONObject()
                        Data.newSongs.forEach { it ->
                            val artist = it.artist
                            songs.put(artist, JSONArray())
                            Data.newSongs.forEach {
                                if (it.artist == artist)
                                    (songs[artist] as JSONArray).put(JSONObject().apply {
                                        put("id", it.id.toString())
                                        put("title", it.title)
                                    })
                            }
                        }

                        VolleyRequest.getInstance().post("$BASE_URL/api/lyricsemotion", HashMap(),
                                                         songs.toString().toByteArray(), object : IVolleyRequest {
                            override fun onSuccess(response: JSONObject?) {
                                Database.addSongs(response!!)
                                Database.getEmotionalSongs()
                                Functions.deleteFromFirstTheSecond(Data.newSongs, Data.emotionalSongs)

                                Database.addNonLyricedSongs(Data.newSongs)
                                Log.e("deleted songs", Arrays.toString(Data.newSongs.toTypedArray()))
                                Data.newSongs.clear()
                            }

                            override fun onFail(error: VolleyError?) {

                            }
                        })
                    }.setNegativeButton("No", null)
                    .create().show()
            }

            return
        } else {
            EasyPermissions.requestPermissions(
                    this, "", STORAGE_PERMESSIONS_REQUEST,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

}