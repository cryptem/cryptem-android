package io.cryptem.app.ui.coin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.AppConfig
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentCoinBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UrlEvent
import java.math.RoundingMode

@AndroidEntryPoint
class CoinFragment : BaseFragment<CoinVM, FragmentCoinBinding>(R.layout.fragment_coin){
    override val viewModel: CoinVM by viewModels()
    val args : CoinFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = args.id
        viewModel.addToPortfolio = args.addToPortfolio
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editAmountExchange.setOnEditorActionListener { v, actionId, event ->
            viewModel.save()
            hideKeyboard(v)
            return@setOnEditorActionListener true
        }

        binding.editAmountWallet.setOnEditorActionListener { v, actionId, event ->
            viewModel.save()
            hideKeyboard(v)
            return@setOnEditorActionListener true
        }

        viewModel.isInPortfolio.observe(viewLifecycleOwner){
            activity?.invalidateOptionsMenu()
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
        if (viewModel.isInPortfolio.value == true) {
            inflater.inflate(R.menu.coin, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_delete -> {
                viewModel.remove()
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

}