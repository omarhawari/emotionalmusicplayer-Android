package emotionalmusicplayer.my_classes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

abstract class MyDialogFragment : DialogFragment() {

    private fun doAll(rootView: View) {
        setupViews(rootView)
        setClickListeners(rootView)
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        doAll(rootView)
    }

    abstract fun setupViews(rootView: View)

    abstract fun setClickListeners(rootView: View)

}
