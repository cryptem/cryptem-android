package io.cryptem.app.ui.portfolio

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPortfolioBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UpdateWidgetEvent
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.ui.portfolio.depositwithdraw.DialogDepositWithdraw
import io.cryptem.app.ui.widgets.PortfolioWidgetProvider


@AndroidEntryPoint
class PortfolioFragment :
    BaseFragment<PortfolioVM, FragmentPortfolioBinding>(R.layout.fragment_portfolio) {
    override val viewModel: PortfolioVM by viewModels()

    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.itemAnimator = null

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.addCoinMode.value == true) {
                    viewModel.addCoinMode.value = false
                    return
                } else {
                    if (!navController().navigateUp()) {
                        activity?.finish()
                    }
                }
            }
        })

        observe(UrlEvent::class) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        }

        observe(UpdateWidgetEvent::class) {
            updateWidget()
        }

        binding.recyclerCoins.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    (recyclerView.layoutManager as GridLayoutManager).let { layoutManager ->
                        visibleItemCount = layoutManager.childCount
                        totalItemCount = layoutManager.itemCount
                        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                        if (!viewModel.loadingCoins.value) {
                            if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                                viewModel.loadCoins()
                            }
                        }
                    }
                }
            }
        })
        observeDepositWithdraw()
    }

    private fun observeDepositWithdraw() {
        setFragmentResultListener(DialogDepositWithdraw.REQUEST_KEY) { key, bundle ->
            val newValue = ((viewModel.depositNumber.value ?: 0L) + bundle.getLong(
                DialogDepositWithdraw.RESULT_VALUE
            ))
            if (newValue >= 0) {
                viewModel.depositNumber.value = newValue
            } else {
                viewModel.depositNumber.value = 0L
            }
            viewModel.savePortfolio()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.portfolio, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                navigate(PortfolioFragmentDirections.actionPortfolioFragmentToPortfolioSettingsFragment())
            }
        }
        return false
    }

    private fun updateWidget() {
        val intent = Intent(requireContext(), PortfolioWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids: IntArray = AppWidgetManager.getInstance(requireContext())
            .getAppWidgetIds(ComponentName(requireContext(), PortfolioWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireContext().sendBroadcast(intent)
    }

}