package io.cryptem.app.ui.coin

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentCoinBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UrlEvent
import java.util.*

@AndroidEntryPoint
class CoinFragment : BaseFragment<CoinVM, FragmentCoinBinding>(R.layout.fragment_coin){
    override val viewModel: CoinVM by viewModels()
    val args : CoinFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = args.id
        viewModel.symbol = args.symbol
        if (args.addToPortfolio){
            viewModel.editMode.value = true
        }
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()

        viewModel.editMode.observeForever {
            if(it){
                showKeyboard(binding.editAmountExchange)
            } else {
                hideKeyboard()
            }
        }

        binding.editAmountWallet.setOnEditorActionListener { v, actionId, event ->
            viewModel.savePortfolio()
            hideKeyboard(v)
            return@setOnEditorActionListener true
        }

        viewModel.isInPortfolio.observe(viewLifecycleOwner){
            if (it && args.addToPortfolio){
                navController().navigateUp()
            }
        }

        observe(UrlEvent::class){
            if (it.url.contains("binance") && !viewModel.prefs.isBinanceRegistered()){
                showBinanceDialog(it.url)
                return@observe
            } else {
                showUrl(it.url)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.coin, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_edit -> {
                viewModel.editMode.value = !viewModel.editMode.value
                true
            }
            else -> false
        }
    }

    fun showBinanceDialog(url: String){
        MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.binance_dialog_title).setMessage(R.string.binance_dialog_message).setPositiveButton(R.string.yes){ _, _ ->
            viewModel.prefs.saveBinanceRegistered()
            showUrl(url)
        }.setNegativeButton(R.string.no){ _, _ ->
            showUrl(viewModel.getBinanceLink())
        }.show()
    }

    fun setupChart(){

        viewModel.chartRadios.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner){
                if (it) {
                    when (index) {
                        0 -> binding.chart.xAxis.valueFormatter = TimeAxisFormatter()
                        else -> binding.chart.xAxis.valueFormatter = DateAxisFormatter()
                    }
                }
            }
        }

        val chart = binding.chart
        chart.description.text = ""
        val xAxis: XAxis = chart.xAxis
        xAxis.textSize = 11f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)
        xAxis.valueFormatter = DateAxisFormatter()

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.textColor = ResourcesCompat.getColor(resources, R.color.black, null)
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = false

        val rightAxis: YAxis = chart.axisRight
        rightAxis.textColor = ResourcesCompat.getColor(resources, R.color.coin_bitcoin, null)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawZeroLine(false)
        rightAxis.isGranularityEnabled = false

        chart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry, h: Highlight) {
                viewModel.setSelectedTimestamp(e.x.toLong())
            }

            override fun onNothingSelected() {
                viewModel.chartSelectedDate.value = null
            }

        })
    }
}