package emotionalmusicplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eltrio.emotionalmusicplayer.R
import emotionalmusicplayer.activities.MainActivity
import emotionalmusicplayer.adapters.SongListAdapter
import emotionalmusicplayer.helpers.Data
import emotionalmusicplayer.models.Song
import emotionalmusicplayer.my_classes.MyAdapter
import emotionalmusicplayer.my_classes.MyFragment
import kotlinx.android.synthetic.main.songs_list_fragment.*

class SongsListFragment : MyFragment() {

    var songsAdapter: SongListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.songs_list_fragment, container, false)
    }

    override fun setupViews() {
        songsAdapter = SongListAdapter(context!!, Data.songs, object : MyAdapter.OnClickListener<Song> {

            override fun onClick(holder: RecyclerView.ViewHolder, position: Int, song: Song) {
                (activity as MainActivity).playSong(song)
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