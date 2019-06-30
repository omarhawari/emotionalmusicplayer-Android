package emotionalmusicplayer.my_classes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class MyFragment : Fragment() {

    private fun doAll() {
        setupViews()
        setClickListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doAll()
    }

    abstract fun setupViews()

    abstract fun setClickListeners()

}
