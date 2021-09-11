package io.cryptem.app.model

import io.cryptem.app.AppConfig
import io.cryptem.app.AppConfig.PORTFOLIO_CACHE_MINUTES
import io.cryptem.app.model.db.PortfolioDatabase
import io.cryptem.app.model.db.entity.PortfolioDbEntity
import io.cryptem.app.model.db.toCoin
import io.cryptem.app.model.db.toUiEntity
import io.cryptem.app.model.ui.*
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.util.L
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepository @Inject constructor(
    val prefs: SharedPrefsRepository,
    val portfolioDb: PortfolioDatabase,
    val marketRepository: MarketRepository,
    val binanceRepository: BinanceRepository
) {

    private var portfolio = Cache(PORTFOLIO_CACHE_MINUTES, this::loadPortfolio)

    suspend fun addPortfolioCoin(coin: Coin, amountExchange: Double, amountWallet: Double) {
        portfolioDb.dao().addPortfolioCoin(
            PortfolioDbEntity(
                id = coin.id,
                symbol = coin.symbol,
                name = coin.name,
                image = coin.image,
                marketCapRank = coin.rank,
                currentPriceBtc = coin.priceBtc?.currentPrice,
                currentPriceUsd = coin.priceUsd?.currentPrice,
                currentPriceCustom = coin.priceCustom?.currentPrice,
                priceChangePercentage24hBtc = coin.priceBtc?.percentChange24h,
                priceChangePercentage7dBtc = coin.priceBtc?.percentChange7d,
                priceChangePercentage30dBtc = coin.priceBtc?.percentChange30d,
                priceChangePercentage24hUsd = coin.priceUsd?.percentChange24h,
                priceChangePercentage7dUsd = coin.priceUsd?.percentChange7d,
                priceChangePercentage30dUsd = coin.priceUsd?.percentChange30d,
                amountExchange = amountExchange,
                amountWallet = amountWallet
            )
        )
        portfolio.clear()
    }

    suspend fun removePortfolioCoin(id: String) {
        portfolioDb.dao().removePortfolioCoin(id)
        portfolio.clear()
    }

    suspend fun getPortfolioCoin(id: String): PortfolioItem? {
        return portfolioDb.dao().getPortfolioCoin(id)?.toUiEntity(prefs.getPortfolioCurrency())
    }

    suspend fun getPortfolio(forceLoad : Boolean) : Portfolio{
        return portfolio.get(forceLoad)
    }

    private suspend fun loadPortfolio(): Portfolio {
        val result = Portfolio(prefs.getPortfolioCurrency(), prefs.getPortfolioDeposit())
        val portfolioItems = portfolioDb.dao().getPortfolioCoins()
        val binanceAccount: BinanceAccount? = if (prefs.isBinanceSyncEnabled()) {
            try {
                binanceRepository.getAll()
            } catch (t: Throwable) {
                L.e(t)
                null
            }
        } else null

        if (portfolioItems.isNotEmpty()) {
            try {
                val coinIds = portfolioItems.joinToString(",") { it.id }
                val coins = marketRepository.loadPortfolioCoins(coinIds, result.currency)

                for (portfolioDbEntity in portfolioItems) {
                    coins.find { it.id == portfolioDbEntity.id }?.let { coin ->
                        portfolioDbEntity.apply {
                            currentPriceBtc = coin.priceBtc?.currentPrice
                            currentPriceUsd = coin.priceUsd?.currentPrice
                            currentPriceCustom = coin.priceCustom?.currentPrice
                            priceChangePercentage24hBtc = coin.priceBtc?.percentChange24h
                            priceChangePercentage7dBtc = coin.priceBtc?.percentChange7d
                            priceChangePercentage30dBtc = coin.priceBtc?.percentChange30d
                            priceChangePercentage24hUsd = coin.priceUsd?.percentChange24h
                            priceChangePercentage7dUsd = coin.priceUsd?.percentChange7d
                            priceChangePercentage30dUsd = coin.priceUsd?.percentChange30d
                        }

                        if (binanceAccount != null) {
                            (binanceAccount.getBalance(coin.symbol) ?: 0.0).let {
                                portfolioDbEntity.amountExchange = it
                            }
                        }

                        portfolioDbEntity.lastUpdate = System.currentTimeMillis()
                        portfolioDb.dao().updatePortfolioCoin(portfolioDbEntity)
                    }
                }
            } catch (t: Throwable) {
                L.e(t)
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
        return result
    }

    suspend fun getPortfolioFromDb(): Portfolio {
        val result = Portfolio(prefs.getPortfolioCurrency(), prefs.getPortfolioDeposit())
        result.items = portfolioDb.dao().getPortfolioCoins().map { dbEntity ->
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
            portfolioDb.dao().getPortfolioCoin(id) != null
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

}