package io.cryptem.app

import android.annotation.SuppressLint
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import io.cryptem.app.model.PortfolioUpdateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    override fun onCreate() {
        super.onCreate()
        instance = this

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PortfolioWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<PortfolioUpdateWorker>(
                1, TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        )
    }
}