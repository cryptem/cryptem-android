package io.cryptem.app.ui.portfolio

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPortfolioBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UpdateWidgetEvent
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.ui.coin.DateAxisFormatter
import io.cryptem.app.ui.widgets.PortfolioWidgetProvider


@AndroidEntryPoint
class PortfolioFragment :
    BaseFragment<PortfolioVM, FragmentPortfolioBinding>(R.layout.fragment_portfolio) {
    override val viewModel: PortfolioVM by viewModels()

    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0

    var intervalMenuItem : MenuItem? = null

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

        viewModel.timeInterval.observe(viewLifecycleOwner){
            intervalMenuItem?.setTitle(it.title)
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
        setupChart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.portfolio, menu)
        intervalMenuItem = menu.findItem(R.id.action_interval)
        intervalMenuItem?.setTitle(viewModel.timeInterval.value.title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                navigate(PortfolioFragmentDirections.actionPortfolioFragmentToPortfolioSettingsFragment())
            }
            R.id.action_interval -> {
                viewModel.toggleTrendTime()
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

    fun setupChart(){

        val chart = binding.header.chart
        chart.description.text = ""
        chart.legend.isEnabled = false
        chart.setDrawBorders(false)
        chart.minOffset = 0f
        //chart.setExtraOffsets(0f,0f,0f,0f)

        val xAxis: XAxis = chart.xAxis
        xAxis.textSize = 11f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)
        xAxis.valueFormatter = DateAxisFormatter()

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.textColor = ResourcesCompat.getColor(resources, R.color.white, null)
        leftAxis.setDrawGridLines(false)
        leftAxis.isGranularityEnabled = false
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis: YAxis = chart.axisRight
        rightAxis.textColor = ResourcesCompat.getColor(resources, R.color.coin_bitcoin, null)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawZeroLine(false)
        rightAxis.isGranularityEnabled = false
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)


    }

}