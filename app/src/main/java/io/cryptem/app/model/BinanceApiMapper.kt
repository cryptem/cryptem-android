package io.cryptem.app.model

import io.cryptem.app.model.binance.dto.AccountSnapshotResponseDto
import io.cryptem.app.model.ui.*

fun AccountSnapshotResponseDto.toUiEntity(): BinanceAccount {
    val result = BinanceAccount()
    snapshotVos?.firstOrNull()?.data?.balances?.let {
        for (i in it) {
            if (i.free + i.locked > 0.0) {
                result.addAsset(i.asset, i.free + i.locked)
            }
        }
    }
    return result
}