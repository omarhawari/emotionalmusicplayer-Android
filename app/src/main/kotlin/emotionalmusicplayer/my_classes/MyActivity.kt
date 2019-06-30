package emotionalmusicplayer.my_classes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class MyActivity : AppCompatActivity() {

    fun doAll() {
        setupViews()
        setClickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    open fun setupViews() {}

    open fun setClickListeners() {}

}