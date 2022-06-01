package io.cryptem.app.model

import io.cryptem.app.model.binance.BinanceApiDef
import io.cryptem.app.model.binance.BinanceVapiDef
import io.cryptem.app.model.ui.BinanceAccount
import javax.inject.Inject

class BinanceRepository @Inject constructor(private val api : BinanceApiDef, private val vapi : BinanceVapiDef) {

    var timestampOffset : Long = 0

    suspend fun getAll() : BinanceAccount{
        if (timestampOffset == 0L){
            getServerTime()
        }
        return api.getAll(timestamp = System.currentTimeMillis() - timestampOffset).toUiEntity()
    }

    suspend fun getServerTime(){
        timestampOffset = System.currentTimeMillis() - (vapi.getServerTime().data ?: 0)
    }
}