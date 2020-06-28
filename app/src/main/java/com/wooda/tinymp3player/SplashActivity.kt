package com.wooda.tinymp3player

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private lateinit var mStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        mStatusText = findViewById(R.id.statusMessage)

        GlobalScope.launch(Dispatchers.Main)
        {
            val fileCount = buildAllMp3FilesAsync().await()
            mStatusText.text = "$fileCount files are loaded."
        }
    }

    private fun buildAllMp3FilesAsync() : Deferred<Int> =
        GlobalScope.async(Dispatchers.IO) {
            Thread.sleep(10_000)
            return@async 10
        }
}