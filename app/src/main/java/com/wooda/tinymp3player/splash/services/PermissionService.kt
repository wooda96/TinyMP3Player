package com.wooda.tinymp3player.splash.services

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.wooda.tinymp3player.splash.SplashPresenter
import java.lang.Exception

internal class PermissionService() {

    private var onPermissionAcquiredCallback: () -> Unit = {}
    private var onPermissionDeniedCallback: () -> Unit = {}

    fun acquireReadExternalStoragePermission(
        requestActivity: Activity,
        onPermissionAcquired: () -> Unit,
        onPermissionDenied: () -> Unit) {

        if (!checkPermissionAcquired(requestActivity)) {
            Log.i(TAG, "Requesting READ_EXTERNAL_STORAGE permission.")
            this.onPermissionAcquiredCallback = onPermissionAcquired
            this.onPermissionDeniedCallback = onPermissionDenied
            requestPermission(requestActivity)
        } else {
            onPermissionAcquired()
            return
        }
    }

    // referenced from: https://stackoverflow.com/questions/43148879/how-to-get-read-external-storage-permissions
    private fun checkPermissionAcquired(requestActivity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = requestActivity.checkSelfPermission((Manifest.permission.READ_EXTERNAL_STORAGE))
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false;
    }

    private fun requestPermission(requestActivity: Activity) {
        try {
            ActivityCompat.requestPermissions(
                requestActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun onPermissionResultReceived(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionAcquiredCallback()
                } else {
                    // Requesting permission is denied.
                    Log.i(TAG, "Requesting permission is denied. Finish application")
                    onPermissionDeniedCallback()
                }
            }
        }
    }

    companion object {
        private const val TAG = "PermissionService"
        private const val READ_STORAGE_PERMISSION_REQUEST_CODE = 101
    }
}