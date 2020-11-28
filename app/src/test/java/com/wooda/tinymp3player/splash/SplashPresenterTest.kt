package com.wooda.tinymp3player.splash

import android.app.Activity
import android.os.Build
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.wooda.tinymp3player.splash.services.PermissionService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SplashPresenterTest {

    private lateinit var splashActivity: SplashActivity
    private lateinit var splashView: SplashPresenter.View
    private lateinit var permissionService: PermissionService
    private lateinit var splashPresenter: SplashPresenter

    @Before
    fun setup() {
        splashActivity = Robolectric.buildActivity(SplashActivity::class.java)
            .create()
            .resume()
            .get()

        splashView = Mockito.mock(SplashPresenter.View::class.java)
        permissionService = Mockito.mock(PermissionService::class.java)

        splashPresenter = SplashPresenter(splashActivity, splashView, permissionService)
    }

    @Test
    fun permissionCheckShouldBeCallOnce() {
        splashPresenter.initializePlayList()
        val mockCallback: () -> Unit = mock()
        Mockito.verify(permissionService, Mockito.times(1))
            .acquireReadExternalStoragePermission(
                any(),
                any(),
                any()
            )
    }
}