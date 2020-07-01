package com.wooda.tinymp3player

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.wooda.tinymp3player.presenter.SplashPresenter
import kotlinx.coroutines.*
import java.lang.Exception

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

        mPresenter = SplashPresenter(this, this)
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