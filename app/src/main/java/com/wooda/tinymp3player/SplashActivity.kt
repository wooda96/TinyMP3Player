package com.wooda.tinymp3player

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_splash)
    }
}