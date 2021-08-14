package io.cryptem.app.model

import io.cryptem.app.model.binance.dto.AccountSnapshotResponseDto
import io.cryptem.app.model.ui.BinanceAccount

fun AccountSnapshotResponseDto.toUiEntity(): BinanceAccount {
    val result = BinanceAccount()
    snapshotVos?.sortedByDescending { it.updateTime }
        ?.firstOrNull { it.type == "spot" }?.data?.balances?.let {
        for (i in it) {
            result.addAsset(i.asset, i.free + i.locked)
        }
    }
    return result
}