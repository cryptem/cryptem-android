package io.cryptem.app.model

import io.cryptem.app.model.binance.BinanceApiDef
import io.cryptem.app.model.binance.dto.AccountSnapshotResponseDto
import io.cryptem.app.model.ui.BinanceAccount
import javax.inject.Inject

class BinanceRepository @Inject constructor(private val api : BinanceApiDef) {

    suspend fun getAccountSnapshot() : BinanceAccount{
        return api.getAccountSnapshot().toUiEntity()
    }
}