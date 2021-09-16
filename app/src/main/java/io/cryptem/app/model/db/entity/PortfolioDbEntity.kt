package io.cryptem.app.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio")
class PortfolioDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "amount_exchange")
    var amountExchange: Double,
    @ColumnInfo(name = "amount_wallet")
    var amountWallet: Double,
    @ColumnInfo(name = "symbol")
    var symbol: String,
    @ColumnInfo(name = "image")
    var image: String? = null,
    @ColumnInfo(name = "market_cap_rank")
    var marketCapRank: Int? = null,
    @ColumnInfo(name = "current_price_btc")
    var currentPriceBtc: Double? = null,
    @ColumnInfo(name = "current_price_usd")
    var currentPriceUsd: Double? = null,
    @ColumnInfo(name = "current_price_custom")
    var currentPriceCustom: Double? = null,
    @ColumnInfo(name = "price_change_percentage_24h_btc")
    var priceChangePercentage24hBtc: Double? = null,
    @ColumnInfo(name = "price_change_percentage_7d_btc")
    var priceChangePercentage7dBtc: Double? = null,
    @ColumnInfo(name = "price_change_percentage_30d_btc")
    var priceChangePercentage30dBtc: Double? = null,
    @ColumnInfo(name = "price_change_percentage_1y_btc")
    var priceChangePercentage1yBtc: Double? = null,
    @ColumnInfo(name = "price_change_percentage_24h_usd")
    var priceChangePercentage24hUsd: Double? = null,
    @ColumnInfo(name = "price_change_percentage_7d_usd")
    var priceChangePercentage7dUsd: Double? = null,
    @ColumnInfo(name = "price_change_percentage_30d_usd")
    var priceChangePercentage30dUsd: Double? = null,
    @ColumnInfo(name = "price_change_percentage_1y_usd")
    var priceChangePercentage1yUsd: Double? = null,
    @ColumnInfo(name = "last_update")
    var lastUpdate: Long = System.currentTimeMillis()
)
