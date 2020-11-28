package com.wooda.tinymp3player.splash

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wooda.tinymp3player.R
import com.wooda.tinymp3player.splash.services.PermissionService

class SplashActivity : SplashPresenter.View, AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
    }

    private lateinit var mStatusText: TextView
    private lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        mStatusText = findViewById(R.id.statusMessage)

        mPresenter = SplashPresenter(this, this, PermissionService())
        mPresenter.initializePlayList()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        mPresenter.onPermissionResultReceived(requestCode, permissions, grantResults)
    }

    override fun updateStatus(statusMessage: String) {
        mStatusText.text = statusMessage
    }

    override fun requestFinish() {
        finish()
    }

}