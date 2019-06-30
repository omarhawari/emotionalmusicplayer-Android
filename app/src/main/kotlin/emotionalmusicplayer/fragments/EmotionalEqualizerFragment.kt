package emotionalmusicplayer.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import com.eltrio.emotionalmusicplayer.R
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import emotionalmusicplayer.my_classes.MyDialogFragment
import kotlinx.android.synthetic.main.fragment_emotional_equalizer.view.*

class EmotionalEqualizerFragment : MyDialogFragment() {

    companion object {
        val TAG = "EmotionalEqualizerFragment"
    }

    val seekBars = arrayListOf<VerticalSeekBar>()

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_emotional_equalizer, container, false).apply {
//            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .create().apply {
                setView(LayoutInflater.from(context).inflate(R.layout.fragment_emotional_equalizer,
                                                             null,
                                                             false).apply {
                    onViewCreated(this, savedInstanceState)
                })
            }
    }

    override fun setupViews(rootView: View) {
        seekBars.addAll(arrayOf(rootView.excited, rootView.happy, rootView.neutral, rootView.sad, rootView.stressed))
    }

    override fun setClickListeners(rootView: View) {

        val map = HashMap<VerticalSeekBar, Int>()

        seekBars.forEach {

            map[it] = 2000

            it.max = 10000

            it.progress = 2000

            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return

                    val prevProgress = map[seekBar]!!

                    val previousOtherProgress = 10000 - prevProgress
                    val currOtherProgress = 10000 - progress

                    map[seekBar as VerticalSeekBar] = progress

                    var allZeros = true

                    seekBars.forEach { it2 ->
                        if (it2!= seekBar && it2.progress > 0) {
                            allZeros = false
                        }
                    }

                    seekBars.forEach { it2 ->
                        if (it2 != seekBar) {
                            it2.progress = if (allZeros) {
                                currOtherProgress / 4
                            } else {
                                ((it2.progress.toFloat() / previousOtherProgress.toFloat()) * currOtherProgress)
                                    .toInt()
                            }
                            map[it2] = it2.progress
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }
    }
}