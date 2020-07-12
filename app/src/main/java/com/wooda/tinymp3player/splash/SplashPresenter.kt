package com.wooda.tinymp3player.splash

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import com.wooda.tinymp3player.splash.model.AudioModel
import com.wooda.tinymp3player.splash.services.PermissionService
import kotlinx.coroutines.*

class SplashPresenter(private val mActivity: Activity, private val mSplashView: View) {

    private val mPermissionService = PermissionService()

    fun initializePlayList() {

        // check permission
        mPermissionService.acquireReadExternalStoragePermission(
            mActivity,
            { initializePlayListWithoutPermissionCheck() },
            {
                Log.i(TAG, "Requesting permission is denied. Finish application")
                mSplashView.requestFinish()
            }
        )
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
        mPermissionService.onPermissionResultReceived(requestCode, permissions, grantResults)
    }

    companion object {
        private const val TAG = "SplashPresenter"
    }

    interface View {
        fun updateStatus(statusMessage: String);
        fun requestFinish()
    }
}