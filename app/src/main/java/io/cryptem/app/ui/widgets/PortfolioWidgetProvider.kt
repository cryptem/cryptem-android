package io.cryptem.app.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.model.PortfolioRepository
import io.cryptem.app.ui.MainActivity
import io.cryptem.app.util.L
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PortfolioWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var repository: PortfolioRepository
    private val externalScope: CoroutineScope = GlobalScope

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        externalScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getPortfolio(false)
            }.onSuccess {
                val views = RemoteViews(
                    context.packageName,
                    R.layout.widget_portfolio
                ).apply {
                    val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                        .let { intent ->
                            PendingIntent.getActivity(context, 0, intent,
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                    PendingIntent.FLAG_IMMUTABLE
                                } else 0)
                        }
                    if (it.valuationPercent >= 0.0){
                        setTextColor(R.id.textValuationPercent, ResourcesCompat.getColor(context.resources, R.color.trend_up_light, null))
                        setImageViewResource(R.id.iconTrend, R.drawable.ic_trend_up_light)
                    } else {
                        setTextColor(R.id.textValuationPercent, ResourcesCompat.getColor(context.resources, R.color.trend_down_light, null))
                        setImageViewResource(R.id.iconTrend, R.drawable.ic_trend_down_light)
                    }

                    setTextViewText(R.id.textValuationPercent, it.getValuationPercentStringAbs())
                    setTextViewText(R.id.textValuationFiat, it.getValuationFiatString())
                    setTextViewText(R.id.textValuationBtc, it.getValuationBtcString())
                    setOnClickPendingIntent(R.id.widget, pendingIntent)
                }
                appWidgetIds.forEach { appWidgetId ->
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
                L.d("Widget updated")
            }.onFailure {
                L.e(it)
            }
        }
    }
}