package io.cryptem.app.model.ui

class BinanceAccount {

    private val balances = HashMap<String, Double>()

    fun addAsset(symbol : String, balance : Double){
        balances[symbol] = balance
    }

    fun getBalance(symbol : String) : Double?{
        return balances[symbol]
    }
}