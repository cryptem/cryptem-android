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
import dagger.hilt.android.AndroidEntryPoint
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
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val focusChangeListener = object: OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    val imm: InputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }

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

        observe(UrlEvent::class){
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.coin, menu)
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
}