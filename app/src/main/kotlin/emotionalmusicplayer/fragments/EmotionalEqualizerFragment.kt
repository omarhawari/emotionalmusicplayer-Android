package emotionalmusicplayer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.SeekBar
import com.eltrio.emotionalmusicplayer.R
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import emotionalmusicplayer.helpers.Functions
import emotionalmusicplayer.my_classes.MyActivity
import emotionalmusicplayer.my_classes.MyDialogFragment
import kotlinx.android.synthetic.main.fragment_emotional_equalizer.*
import org.json.JSONObject

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EmotionalEqualizerFragment : MyDialogFragment() {

    companion object {
        val TAG = "EmotionalEqualizerFragment"
    }

    lateinit var chartFragment: EmotionalChartFragment

    val map = HashMap<VerticalSeekBar, Int>()


    val seekBars = arrayListOf<VerticalSeekBar>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_emotional_equalizer, container, false)
    }

    override fun setupViews(rootView: View) {
        seekBars.addAll(arrayOf(excited, happy, neutral, sad, stressed))

        seekBars.forEach {

            map[it] = 2000

            it.max = 10000

            it.progress = 2000
        }
        chartFragment = EmotionalChartFragment().apply {
            arguments = Bundle().apply { putString("emotion", getEmotionJSON().toString()) }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_place_holder, chartFragment).commit()
    }

    fun getEmotionJSON(): JSONObject {
        return JSONObject().apply {
            put("excited", (excited.progress.toFloat()/ 10000F))
            put("happy", happy.progress.toFloat() / 10000F)
            put("neutral", neutral.progress.toFloat() / 10000F)
            put("sad", sad.progress.toFloat() / 10000F)
            put("stressed", stressed.progress.toFloat() / 10000F)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    override fun setClickListeners(rootView: View) {
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

                    chartFragment.updateChart(getEmotionJSON())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }

        predict.setOnClickListener {
            dismiss()
            Functions.showPredictedList(activity!! as MyActivity, getEmotionJSON())
        }

    }
}