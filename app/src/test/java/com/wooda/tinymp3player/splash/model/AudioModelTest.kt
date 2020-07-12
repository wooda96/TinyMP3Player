package com.wooda.tinymp3player.splash.model

import com.wooda.tinymp3player.splash.model.AudioModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AudioModelTest {
    @Test
    fun constructionTest() {
        val audioModel = AudioModel(
            "/sdcard/Music/test.mp3",
            "SampleMusic",
            "SampleAlbum",
            "SampleArtist"
        )

        assertEquals("/sdcard/Music/test.mp3", audioModel.path)
        assertEquals("SampleMusic", audioModel.name)
        assertEquals("SampleAlbum", audioModel.album)
        assertEquals("SampleArtist", audioModel.artist)
    }
}