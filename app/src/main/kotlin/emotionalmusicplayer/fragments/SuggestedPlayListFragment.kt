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
import emotionalmusicplayer.my_classes.MyDialogFragment
import kotlinx.android.synthetic.main.fragment_suggested_playlist.view.*
import org.json.JSONObject

class SuggestedPlayListFragment : MyDialogFragment() {

    companion object {
        val TAG = "SuggestedPlayListFragment"
    }

    lateinit var songsAdapter: SongListAdapter
    lateinit var emotionsJSON: JSONObject

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        emotionsJSON = JSONObject(arguments!!.getString("emotion"))
        return inflater.inflate(R.layout.fragment_suggested_playlist, container, false)
    }

    override fun setupViews(rootView: View) {
        songsAdapter = SongListAdapter(context!!,
                                       Data.suggestedSongs,
                                       object : MyAdapter.OnClickListener<Song> {

                                           override fun onClick(holder: RecyclerView.ViewHolder,
                                                                position: Int,
                                                                song: Song) {
                                               (activity as MainActivity).playSong(song, songsAdapter.data)
                                               dialog.dismiss()
                                           }
                                       })
        rootView.suggested_songs_rv.layoutManager = LinearLayoutManager(context)
        rootView.suggested_songs_rv.adapter = songsAdapter

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_place_holder, EmotionalChartFragment().apply {
                arguments = Bundle().apply { putString("emotion", emotionsJSON.toString()) }
            }).commit()
    }

    override fun setClickListeners(rootView: View) {

    }

    override fun onResume() {
        super.onResume()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}