package emotionalmusicplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eltrio.emotionalmusicplayer.R
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import emotionalmusicplayer.my_classes.MyFragment
import kotlinx.android.synthetic.main.fragment_emotional_chart.*
import org.json.JSONObject

class EmotionalChartFragment : MyFragment() {

    lateinit var emotions: JSONObject

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        emotions = JSONObject(arguments!!.getString("emotion"))
        return inflater.inflate(R.layout.fragment_emotional_chart, null, false)
    }

    override fun setupViews() {
        pie_chart.setUsePercentValues(true)
        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry((emotions["excited"] as Double).toFloat() * 100, "Excited"))
        entries.add(PieEntry((emotions["sad"] as Double).toFloat() * 100, "Sad"))
        entries.add(PieEntry((emotions["stressed"] as Double).toFloat() * 100, "Stressed"))
        entries.add(PieEntry((emotions["neutral"] as Double).toFloat() * 100, "Neutral"))
        entries.add(PieEntry((emotions["happy"] as Double).toFloat() * 100, "Happy"))

        val data = PieData(PieDataSet(entries, "").apply {
            colors = ColorTemplate.JOYFUL_COLORS.toMutableList()
        })

        pie_chart.data = data

    }

    override fun setClickListeners() {

    }

}