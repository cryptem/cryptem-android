package io.cryptem.app.ui.portfolio

import io.cryptem.app.R
import io.cryptem.app.model.ui.Portfolio
import io.cryptem.app.model.ui.PortfolioItem
import kodebase.adapter.RecyclerLayoutStrategy
import java.lang.IllegalArgumentException

class PortfolioLayoutStrategy : RecyclerLayoutStrategy {
    override fun getLayoutId(item: Any): Int {
        return when (item) {
            is Portfolio -> R.layout.layout_portfolio_header
            is PortfolioItem -> R.layout.item_portfolio
            else -> throw IllegalArgumentException("Layout mapping not found")
        }
    }
}