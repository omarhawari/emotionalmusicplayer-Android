package emotionalmusicplayer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.eltrio.emotionalmusicplayer.R
import emotionalmusicplayer.activities.MainActivity
import emotionalmusicplayer.models.Song
import emotionalmusicplayer.my_classes.MyAdapter
import emotionalmusicplayer.my_classes.MyViewHolder
import kotlinx.android.synthetic.main.rv_song.view.*

class SongListAdapter(context: Context, data: ArrayList<Song>, onClickListener: OnClickListener<Song>) :
    MyAdapter<SongListAdapter.Holder, Song>(context, data, onClickListener) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.rv_song, viewGroup, false))
    }

    override fun onBindViewHolderItem(holder: Holder, song: Song) {
        if ((context as MainActivity).song != null && context.song!!.id == song.id) {
            holder.playGif.visibility = View.VISIBLE
            holder.playIcon.visibility = View.GONE
            holder.playGif.setImageResource(R.drawable.music_playing)
            holder.playGif.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            holder.playGif.visibility = View.GONE
            holder.playIcon.visibility = View.VISIBLE

        }
        holder.songTitle.text = song.title
        holder.artist.text = song.artist
        holder.songTitle.isSelected = true
        holder.songTitle.invalidate()
    }

    class Holder(rootView: View) : MyViewHolder(rootView) {
        val play = rootView.play
        val playIcon = rootView.play_icon
        val songTitle = rootView.song_title
        val artist = rootView.song_artist
        val playGif = rootView.play_gif
    }

}