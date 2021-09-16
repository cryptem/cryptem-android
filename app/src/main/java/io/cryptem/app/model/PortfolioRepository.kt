package io.cryptem.app.model

import android.graphics.Color
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.cryptem.app.AppConfig.PORTFOLIO_CACHE_MINUTES
import io.cryptem.app.model.db.PortfolioDatabase
import io.cryptem.app.model.db.entity.PortfolioDbEntity
import io.cryptem.app.model.db.entity.PortfolioSnapshotEntity
import io.cryptem.app.model.db.toCoin
import io.cryptem.app.model.db.toUiEntity
import io.cryptem.app.model.ui.*
import io.cryptem.app.util.L
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepository @Inject constructor(
    private val prefs: SharedPrefsRepository,
    private val db: PortfolioDatabase,
    private val marketRepo: MarketRepository,
    private val binanceRepo: BinanceRepository
) {
    private val mutex = Mutex()
    private var portfolio = Cache(PORTFOLIO_CACHE_MINUTES, this::loadPortfolio)

    suspend fun addPortfolioCoin(coin: Coin, amountExchange: Double, amountWallet: Double) {
        db.dao().addPortfolioCoin(
            PortfolioDbEntity(
                id = coin.id,
                symbol = coin.symbol,
                name = coin.name,
                image = coin.image,
                marketCapRank = coin.rank,
                currentPriceBtc = coin.priceBtc?.currentPrice,
                currentPriceUsd = coin.priceUsd?.currentPrice,
                currentPriceCustom = coin.priceCustom?.currentPrice,
                priceChangePercentage24hBtc = coin.getPercentBtc(TimeInterval.DAY),
                priceChangePercentage7dBtc = coin.getPercentBtc(TimeInterval.WEEK),
                priceChangePercentage30dBtc = coin.getPercentBtc(TimeInterval.MONTH),
                priceChangePercentage1yBtc = coin.getPercentBtc(TimeInterval.YEAR),
                priceChangePercentage24hUsd = coin.getPercentUsd(TimeInterval.DAY),
                priceChangePercentage7dUsd = coin.getPercentUsd(TimeInterval.WEEK),
                priceChangePercentage30dUsd = coin.getPercentUsd(TimeInterval.MONTH),
                priceChangePercentage1yUsd = coin.getPercentUsd(TimeInterval.YEAR),
                amountExchange = amountExchange,
                amountWallet = amountWallet
            )
        )
        prefs.setPortfolioLastAdd()
        portfolio.clear()
    }

    suspend fun removePortfolioCoin(id: String) {
        db.dao().removePortfolioCoin(id)
        portfolio.clear()
    }

    suspend fun getPortfolioCoin(id: String): PortfolioItem? {
        return db.dao().getPortfolioCoin(id)?.toUiEntity(prefs.getPortfolioCurrency())
    }

    suspend fun getPortfolio(forceLoad: Boolean): Portfolio {
        // Mutex is necessary to proper sync app with work manager
        mutex.withLock {
            return portfolio.get(forceLoad)
        }
    }

    private suspend fun loadPortfolio(): Portfolio {
        val result = Portfolio(prefs.getPortfolioCurrency(), prefs.getPortfolioDeposit())
        val portfolioItems = db.dao().getPortfolioCoins()
        val binanceAccount: BinanceAccount? = if (prefs.isBinanceSyncEnabled()) {
            try {
                binanceRepo.getAll()
            } catch (t: Throwable) {
                L.e(t)
                null
            }
        } else null

        if (portfolioItems.isNotEmpty()) {
            val coinIds = portfolioItems.joinToString(",") { it.id }
            val coins = marketRepo.loadPortfolioCoins(coinIds, result.currency)

            for (portfolioDbEntity in portfolioItems) {
                coins.find { it.id == portfolioDbEntity.id }?.let { coin ->
                    portfolioDbEntity.apply {
                        currentPriceBtc = coin.priceBtc?.currentPrice
                        currentPriceUsd = coin.priceUsd?.currentPrice
                        currentPriceCustom = coin.priceCustom?.currentPrice
                        priceChangePercentage24hBtc = coin.getPercentBtc(TimeInterval.DAY)
                        priceChangePercentage7dBtc = coin.getPercentBtc(TimeInterval.WEEK)
                        priceChangePercentage30dBtc = coin.getPercentBtc(TimeInterval.MONTH)
                        priceChangePercentage1yBtc = coin.getPercentBtc(TimeInterval.YEAR)
                        priceChangePercentage24hUsd = coin.getPercentUsd(TimeInterval.DAY)
                        priceChangePercentage7dUsd = coin.getPercentUsd(TimeInterval.WEEK)
                        priceChangePercentage30dUsd = coin.getPercentUsd(TimeInterval.MONTH)
                        priceChangePercentage1yUsd = coin.getPercentUsd(TimeInterval.YEAR)
                    }

                    if (binanceAccount != null) {
                        (binanceAccount.getBalance(coin.symbol) ?: 0.0).let {
                            portfolioDbEntity.amountExchange = it
                        }
                    }

                    portfolioDbEntity.lastUpdate = System.currentTimeMillis()
                    db.dao().updatePortfolioCoin(portfolioDbEntity)
                }
            }
        }

        result.items = portfolioItems.map { dbEntity ->
            PortfolioItem(
                coin = dbEntity.toCoin(),
                amountExchange = dbEntity.amountExchange,
                amountWallet = dbEntity.amountWallet,
                currency = result.currency
            )
        }
        result.recalculate()
        saveSnapshot(result)
        return result
    }

    private suspend fun saveSnapshot(portfolio: Portfolio) {
        if (canSaveSnapshot(portfolio)) {
            db.dao().addSnapshot(
                PortfolioSnapshotEntity(
                    deposit = portfolio.deposit,
                    fiat = portfolio.valuationFiat.toLong(),
                    btc = portfolio.valuationBtc,
                    percent = portfolio.valuationPercent,
                )
            )
        }
    }

    private suspend fun canSaveSnapshot(portfolio: Portfolio): Boolean {
        val lastPortfolioAdd = prefs.getPortfolioLastAdd()
        val minimumInterval = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        val minimumSnapshotIntervalOk = System.currentTimeMillis() - (db.dao().getLastSnapshot() ?: 0) >= minimumInterval
        // Avoid chart glitches during initial portfolio setup
        val minimumAddCoinIntervalOk = System.currentTimeMillis() - lastPortfolioAdd >= minimumInterval

        return portfolio.items.isNotEmpty() && minimumSnapshotIntervalOk && (lastPortfolioAdd == 0L || minimumAddCoinIntervalOk)
    }

    suspend fun getPortfolioFromDb(): Portfolio {
        val result = Portfolio(prefs.getPortfolioCurrency(), prefs.getPortfolioDeposit())
        result.items = db.dao().getPortfolioCoins().map { dbEntity ->
            PortfolioItem(
                coin = dbEntity.toCoin(),
                amountExchange = dbEntity.amountExchange,
                amountWallet = dbEntity.amountWallet,
                currency = result.currency
            )
        }
        result.recalculate()
        return result
    }

    suspend fun isInPortfolio(id: String): Boolean {
        return if (portfolio.hasData()) {
            portfolio.get().items.firstOrNull { it.coin.id == id } != null
        } else {
            db.dao().getPortfolioCoin(id) != null
        }
    }

    fun getPortfolioCurrency(): Currency {
        return prefs.getPortfolioCurrency()
    }

    fun setPortfolioDeposit(amount: Long) {
        prefs.savePortfolioDeposit(amount)
        portfolio.clear()
    }

    fun setPortfolioCurrency(currency: Currency) {
        prefs.savePortfolioCurrency(currency)
        portfolio.clear()
    }

    fun getPortfolioDeposit(): Long {
        return prefs.getPortfolioDeposit()
    }

    suspend fun getChart(interval: TimeInterval): LineData {

        val fiatData = ArrayList<Entry>()
        val btcData = ArrayList<Entry>()

        val snapshots =
            db.dao().getSnapshots(System.currentTimeMillis() - interval.miliseconds)

        var previousTimestamp = 0L
        snapshots.forEach {
            if (it.timestamp > previousTimestamp + (interval.miliseconds / 30)) {
                fiatData.add(Entry(it.timestamp.toFloat(), it.fiat.toFloat()))
                btcData.add(Entry(it.timestamp.toFloat(), it.btc.toFloat()))
                previousTimestamp = it.timestamp
            }
        }

        val startTime = System.currentTimeMillis() - interval.miliseconds
        if (fiatData.isNotEmpty()) {
            fiatData.add(0, fiatData[0].copy().apply { x = startTime.toFloat() })
            btcData.add(0, btcData[0].copy().apply { x = startTime.toFloat() })
        }

        val set1 = LineDataSet(fiatData, "USD")

        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.color = Color.WHITE
        set1.lineWidth = 1f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        val set2 = LineDataSet(btcData, "BTC")
        set2.axisDependency = YAxis.AxisDependency.RIGHT
        set2.color = Color.parseColor("#FFC800")
        set2.lineWidth = 1f
        set2.setDrawCircles(false)
        set2.setDrawValues(false)
        set2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        val data = LineData(set1, set2)

        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        data.isHighlightEnabled = false
        return data
    }

    suspend fun clearChart() {
        db.dao().clearSnapshots()
    }
}