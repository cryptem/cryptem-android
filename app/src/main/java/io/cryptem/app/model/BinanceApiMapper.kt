package io.cryptem.app.model

import io.cryptem.app.model.binance.dto.GetAllResponseItemDto
import io.cryptem.app.model.ui.BinanceAccount

fun List<GetAllResponseItemDto>.toUiEntity(): BinanceAccount {
    val result = BinanceAccount()
    for (i in this.filter { it.free != 0.0}) {
        result.addAsset(i.coin, i.free + i.locked)
    }
    return result
}