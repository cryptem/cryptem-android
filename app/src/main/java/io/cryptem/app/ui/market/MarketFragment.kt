package io.cryptem.app.ui.market

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
    var favoritesMenuItem : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.market, menu)
        favoritesMenuItem = menu.findItem(R.id.action_favorites)
        handleFavoritesIcon()
    }

    private fun handleFavoritesIcon(){
        if(viewModel.favoriteMode.value){
            favoritesMenuItem?.setIcon(R.drawable.ic_action_favorites_on)
        } else {
            favoritesMenuItem?.setIcon(R.drawable.ic_action_favorites_off)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_favorites -> {
                viewModel.favoriteMode.value = !viewModel.favoriteMode.value
                if (viewModel.favoriteMode.value){
                    viewModel.loadFavorites(true)
                }
                handleFavoritesIcon()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMarket.itemAnimator = null

        binding.recyclerMarket.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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