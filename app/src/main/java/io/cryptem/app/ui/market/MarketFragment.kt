package io.cryptem.app.ui.market

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentMarketBinding
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.ui.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class MarketFragment : BaseFragment<MarketVM, FragmentMarketBinding>(R.layout.fragment_market){
    override val viewModel: MarketVM by viewModels()

    var firstVisibleItem:Int = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.itemAnimator = null

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    (recyclerView.layoutManager as LinearLayoutManager).let { layoutManager ->
                        visibleItemCount = layoutManager.childCount
                        totalItemCount = layoutManager.itemCount
                        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                        if (!viewModel.reloading.value && !viewModel.loading.value) {
                            if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                                viewModel.loadCoins(false)
                            }
                        }
                    }
                }
            }
        })
    }
}