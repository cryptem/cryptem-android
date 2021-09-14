package io.cryptem.app.model

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.cryptem.app.util.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PortfolioDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val portfolioRepository: PortfolioRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        L.d("Portfolio Worker executed")
        return try {
            portfolioRepository.getPortfolio(false)
            L.d("Portfolio Worker success!")
            Result.success()
        } catch (t: Throwable) {
            L.e(t)
            Result.failure()
        }
    }
}