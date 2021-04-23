package io.cryptem.app.ui.portfolio

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPortfolioBinding
import io.cryptem.app.ui.base.BaseFragment


@AndroidEntryPoint
class PortfolioFragment : BaseFragment<PortfolioVM, FragmentPortfolioBinding>(R.layout.fragment_portfolio){
    override val viewModel: PortfolioVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.header.editDeposit.setOnEditorActionListener { v, actionId, event ->
            viewModel.depositEditor.value = false
            viewModel.savePortfolio()
            hideKeyboard(binding.header.editDeposit)
            return@setOnEditorActionListener true
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object:
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.addCoinMode.value == true){
                    viewModel.addCoinMode.value = false
                    return
                }else{
                    if (!navController().navigateUp()){
                        activity?.finish()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.hodl, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_edit -> {
                if (!viewModel.depositEditor.value) {
                    binding.header.editDeposit.apply {
                        requestFocus()
                        setSelection(text?.length ?: 0)
                    }
                    showKeyboard()
                } else {
                    hideKeyboard(binding.header.editDeposit)
                }
                viewModel.toggleDepositEditor()
                return true
            }
        }
        return false
    }

}