package com.wooda.tinymp3player.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.wooda.tinymp3player.model.AudioModel
import kotlinx.coroutines.*
import java.lang.Exception

class SplashPresenter(val mActivity: Activity, val mSplashView: View) {

    fun initializePlayList() {

        // check permission
        if (!checkPermissionAcquired()) {
            Log.i(TAG, "Requesting READ_EXTERNAL_STORAGE permission.")
            requestPermission()
        } else {
            initializePlayListWithoutPermissionCheck()
        }
    }

    private fun initializePlayListWithoutPermissionCheck() {
        GlobalScope.launch(Dispatchers.Main)
        {
            Log.i(TAG, "Starting initialize playlist.")
            val fileCount = buildAllMp3FilesAsync().await()
            mSplashView.updateStatus("$fileCount files are loaded.")
        }
    }

    private fun buildAllMp3FilesAsync() : Deferred<Int> =
        GlobalScope.async(Dispatchers.IO) {
            val mediaFiles = getAllAudioFromDevice(mActivity)
            return@async mediaFiles.size
        }

    // referenced from: https://stackoverflow.com/questions/39461954/list-all-mp3-files-in-android
    private fun getAllAudioFromDevice(context: Context) : List<AudioModel> {
        val result = mutableListOf<AudioModel>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        val projection = arrayOf<String>(
            MediaStore.Audio.AudioColumns.DATA, // todo: check deprecated
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val c = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null)

        if (c != null) {
            while (c.moveToNext()) {
                val path = c.getString(0)
                val album = c.getString(1)
                val artist = c.getString(2)
                val name = path.substring(path.lastIndexOf("/") + 1)

                val audioModel = AudioModel(
                    path,
                    name,
                    album,
                    artist
                )

                Log.d(TAG, "Media file is found: $audioModel")
                result.add(audioModel)
            }
        }
        return result
    }

    fun onPermissionResultReceived(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializePlayListWithoutPermissionCheck()
                } else {
                    // Requesting permission is denied.
                    Log.i(TAG, "Requesting permission is denied. Finish application")
                    mSplashView.requestFinish()
                }
            }
        }
    }

    // referenced from: https://stackoverflow.com/questions/43148879/how-to-get-read-external-storage-permissions
    private fun checkPermissionAcquired(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = mActivity.checkSelfPermission((Manifest.permission.READ_EXTERNAL_STORAGE))
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false;
    }

    private fun requestPermission() {
        try {
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    companion object {
        private const val TAG = "SplashPresenter"
        private const val READ_STORAGE_PERMISSION_REQUEST_CODE = 101
    }

    interface View {
        fun updateStatus(statusMessage: String);
        fun requestFinish()
    }
}