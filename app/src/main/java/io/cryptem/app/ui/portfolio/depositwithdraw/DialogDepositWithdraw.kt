package io.cryptem.app.ui.portfolio.depositwithdraw

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.DialogDepositWithdrawBinding
import kodebase.view.KodebaseDialogFragment

@AndroidEntryPoint
class DialogDepositWithdraw : KodebaseDialogFragment<DepositWithdrawVM, DialogDepositWithdrawBinding>(R.layout.dialog_deposit_withdraw) {
    override val viewModel: DepositWithdrawVM by viewModels()
    val args : DialogDepositWithdrawArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        viewModel.currency.value = args.currency
        showKeyboard(binding.editValue)
        binding.editValue.setOnEditorActionListener { v, actionId, event ->
            hideKeyboard(binding.editValue)
            setFragmentResult(REQUEST_KEY, Bundle().apply {
                putLong(RESULT_VALUE, if (viewModel.isDeposit.value) viewModel.valueNumber.value else -viewModel.valueNumber.value)
            })
            dismiss()
            return@setOnEditorActionListener true
        }
    }

    companion object{
        const val REQUEST_KEY = "REQUEST_DEPOSIT_WITHDRAW"
        const val RESULT_VALUE = "VALUE"
    }
}