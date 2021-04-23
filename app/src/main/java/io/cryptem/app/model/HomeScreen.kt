package io.cryptem.app.model

import io.cryptem.app.R

enum class HomeScreen(val id : Int) {
    MARKET(R.id.marketFragment),
    PORTFOLIO(R.id.portfolioFragment),
    MAP(R.id.mapFragment),
    PAY(R.id.payFragment),
    PAYMENT_REQUEST(R.id.requestFragment)
}