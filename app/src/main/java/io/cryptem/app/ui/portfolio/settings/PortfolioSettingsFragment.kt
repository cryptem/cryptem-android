package io.cryptem.app.ui.portfolio.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPortfolioSettingsBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.ScanQrEvent
import io.cryptem.app.ui.portfolio.depositwithdraw.DepositWithdrawMode
import io.cryptem.app.ui.portfolio.depositwithdraw.DialogDepositWithdraw
import io.cryptem.app.ui.portfolio.settings.event.BinanceTestEvent
import io.cryptem.app.ui.qrscanner.QrScannerActivity
import io.cryptem.app.ui.wallets.WalletFragment

@AndroidEntryPoint
class PortfolioSettingsFragment : BaseFragment<PortfolioSettingsVM, FragmentPortfolioSettingsBinding>(R.layout.fragment_portfolio_settings) {
    override val viewModel: PortfolioSettingsVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editDeposit.setOnEditorActionListener { v, actionId, event ->
            viewModel.savePortfolio()
            hideKeyboard(binding.editDeposit)
            return@setOnEditorActionListener true
        }

        binding.editBinanceSecretKey.setOnEditorActionListener { v, actionId, event ->
            viewModel.testBinance()
            hideKeyboard(binding.editBinanceSecretKey)
            return@setOnEditorActionListener true
        }

        observe(ScanQrEvent::class){
            scanQr()
        }

        observe(BinanceTestEvent::class){
            if (it.success){
                Toast.makeText(requireContext(), R.string.portfolio_settings_binance_test_success, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), R.string.portfolio_settings_binance_test_failed, Toast.LENGTH_LONG).show()
            }
        }
        observeDepositWithdraw()
    }

    private fun observeDepositWithdraw() {
        setFragmentResultListener(DialogDepositWithdraw.REQUEST_KEY) { key, bundle ->
            val newValue = ((viewModel.deposit.value?.toLongOrNull() ?: 0L) + bundle.getLong(
                DialogDepositWithdraw.RESULT_VALUE
            ))
            if (newValue >= 0) {
                viewModel.deposit.value = newValue.toString()
            } else {
                viewModel.deposit.value = "0"
            }
            viewModel.savePortfolio()
        }
    }

    private fun scanQr() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ), WalletFragment.REQUEST_CAMERA_PERMISSION
            )
        } else {
            runQrReader()
        }
    }

    private fun runQrReader(){
        IntentIntegrator.forSupportFragment(this).apply {
            captureActivity = QrScannerActivity::class.java
            setOrientationLocked(false)
            setPrompt(getString(R.string.portfolio_settings_scan_binance_api_key))
        }.initiateScan(listOf((IntentIntegrator.QR_CODE)))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == WalletFragment.REQUEST_CAMERA_PERMISSION && grantResults.first() == PackageManager.PERMISSION_GRANTED){
            runQrReader()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data).contents?.let {
                viewModel.parseBinanceKeys(it)
            }
        }
    }
}