package io.cryptem.app.model

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

    private var portfolio : Portfolio? = null

    suspend fun addPortfolioCoin(coin: Coin, amountExchange: Double, amountWallet : Double) {
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
        portfolio = null
    }

    suspend fun removePortfolioCoin(id: String) {
        portfolioDb.dao().removePortfolioCoin(id)
        portfolio = null
    }

    suspend fun getPortfolioCoin(id: String) : PortfolioItem?{
        return portfolioDb.dao().getPortfolioCoin(id)?.toUiEntity(prefs.getPortfolioCurrency())
    }

    suspend fun getPortfolio(forceLoad : Boolean): Portfolio {
        if (portfolio != null && !forceLoad){
            return portfolio!!
        }
        val result = Portfolio(prefs.getPortfolioCurrency(), prefs.getPortfolioDeposit())
        val portfolioItems = portfolioDb.dao().getPortfolioCoins()
        val binanceAccount : BinanceAccount? = if (prefs.isBinanceSyncEnabled()){
            try {
                binanceRepository.getAll()
            } catch (t : Throwable){
                L.e(t)
                null
            }
        } else null

        if (portfolioItems.isNotEmpty()) {
            try {
                val coinIds = portfolioItems.joinToString(",") { it.id }
                val coinsBtc =
                    marketRepository.loadCoins(ids = coinIds, customCurrency = Currency.BTC)
                val coinsUsd =
                    marketRepository.loadCoins(ids = coinIds, customCurrency = Currency.USD)
                val coinsCustom =
                    marketRepository.loadCoinsPrice(ids = coinIds, customCurrency = result.currency)

                for (portfolioDbEntity in portfolioItems) {
                    coinsBtc.find { it.id == portfolioDbEntity.id }?.let { coin ->
                        coin.priceUsd = coinsUsd.find { it.id == portfolioDbEntity.id }?.priceUsd
                        coin.priceCustom = CoinPrice(currentPrice = coinsCustom[coin.id]?.get(result.currency.code.toLowerCase(Locale.getDefault())))

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

                        portfolioDbEntity.lastUpdate = System.currentTimeMillis()

                        binanceAccount?.getBalance(coin.symbol)?.let {
                            portfolioDbEntity.amountExchange = it
                        }

                        portfolioDb.dao().updatePortfolioCoin(portfolioDbEntity)
                    }
                }
            } catch (t : Throwable){
                L.e(t)
            }
        }

        result.items = portfolioItems.map { dbEntity ->
            PortfolioItem(coin = dbEntity.toCoin(),
                amountExchange = dbEntity.amountExchange,
                amountWallet = dbEntity.amountWallet,
                currency = result.currency)
        }
        result.recalculate()
        portfolio = result
        return portfolio!!
    }

    suspend fun getPortfolioFromDb(): Portfolio {
        val result = Portfolio(prefs.getPortfolioCurrency(), prefs.getPortfolioDeposit())
        result.items = portfolioDb.dao().getPortfolioCoins().map { dbEntity ->
            PortfolioItem(coin = dbEntity.toCoin(),
                amountExchange = dbEntity.amountExchange,
                amountWallet = dbEntity.amountWallet,
                currency = result.currency)
        }
        result.recalculate()
        return result
    }

    fun getPortfolioCurrency(): Currency {
        return prefs.getPortfolioCurrency()
    }

    fun setPortfolioDeposit(amount: Long) {
        prefs.savePortfolioDeposit(amount)
    }

    fun setPortfolioCurrency(currency: Currency) {
        prefs.savePortfolioCurrency(currency)
        portfolio = null
    }

    fun getPortfolioDeposit(): Long {
        return prefs.getPortfolioDeposit()
    }

}