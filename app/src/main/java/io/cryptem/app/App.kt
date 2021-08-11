package io.cryptem.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var instance : App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}