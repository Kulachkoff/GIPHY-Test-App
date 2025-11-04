package com.chililabs.giphytest

import android.app.Application
import com.giphy.sdk.ui.Giphy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GiphyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Giphy.configure(this, BuildConfig.GIPHY_API_KEY)
    }
}