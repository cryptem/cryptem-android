package io.cryptem.app.ui.market

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentMarketBinding
import io.cryptem.app.ui.base.BaseFragment

@AndroidEntryPoint
class MarketFragment : BaseFragment<MarketVM, FragmentMarketBinding>(R.layout.fragment_market){
    override val viewModel: MarketVM by viewModels()

}