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
import kotlinx.coroutines.*
import java.lang.Exception

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private const val READ_STORAGE_PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var mStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        mStatusText = findViewById(R.id.statusMessage)

        if (!checkPermissionForReadExternalStorage()) {
            Log.i(TAG, "Requesting READ_EXTERNAL_STORAGE permission.")
            requestPermissionForReadExternalStorage()
        } else {
            startInitialize()
        }
    }

    private fun startInitialize() {
        GlobalScope.launch(Dispatchers.Main)
        {
            val fileCount = buildAllMp3FilesAsync().await()
            mStatusText.text = "$fileCount files are loaded."
        }
    }

    private fun buildAllMp3FilesAsync() : Deferred<Int> =
        GlobalScope.async(Dispatchers.IO) {
            val mediaFiles = getAllAudioFromDevice(applicationContext)
            return@async mediaFiles.size
        }

    // referenced from: https://stackoverflow.com/questions/43148879/how-to-get-read-external-storage-permissions
    private fun checkPermissionForReadExternalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = applicationContext.checkSelfPermission((Manifest.permission.READ_EXTERNAL_STORAGE))
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false;
    }

    private fun requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startInitialize()
                } else {
                    // Requesting permission is denied.
                    Log.i(TAG, "Requesting permission is denied. Finish application")
                    finish()
                }
            }
        }
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
}