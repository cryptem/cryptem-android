package io.cryptem.app.model

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.cryptem.app.util.L

@HiltWorker
class PortfolioUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val portfolioRepository: PortfolioRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        L.d("PortfolioUpdateWorker started")
        return try {
            portfolioRepository.getPortfolio(false)
            L.d("PortfolioUpdateWorker success")
            Result.success()
        } catch (t: Throwable) {
            L.d("PortfolioUpdateWorker failure")
            L.e(t)
            Result.failure()
        }
    }
}