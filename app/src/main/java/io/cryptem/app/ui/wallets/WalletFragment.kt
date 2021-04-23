package io.cryptem.app.ui.wallets

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentWalletBinding
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.qrscanner.QrScannerActivity
import io.cryptem.app.ui.wallets.event.RemoveWalletEvent


@AndroidEntryPoint
class WalletFragment : BaseFragment<WalletVM, FragmentWalletBinding>(R.layout.fragment_wallet){
    override val viewModel: WalletVM by viewModels()
    val args : WalletFragmentArgs by navArgs()

    companion object{
        const val REQUEST_CAMERA_PERMISSION = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.id != 0L){
            viewModel.loadWallet(args.id)
        } else {
            viewModel.wallet.value = Wallet()
            viewModel.selectedCoin.value = args.coin
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(ScanAddressEvent::class){
            scanQr()
        }
        observe(RemoveWalletEvent::class){
            showRemoveDialog()
        }
    }

    fun showRemoveDialog(){
        MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.remove).setMessage(R.string.wallet_remove_dialog).setPositiveButton(R.string.yes){ _, _ ->
            viewModel.confirmRemove()
        }.setNegativeButton(R.string.no){ _, _ ->

        }.show()
    }

    private fun runQrReader(){
        IntentIntegrator.forSupportFragment(this).apply {
            captureActivity = QrScannerActivity::class.java
            setOrientationLocked(false)
            setPrompt(getString(R.string.scan_address_prompt, viewModel.wallet.value?.coin?.title))
        }.initiateScan(IntentIntegrator.QR_CODE_TYPES)
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
                ), REQUEST_CAMERA_PERMISSION
            )
        } else {
            runQrReader()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.first() == PackageManager.PERMISSION_GRANTED){
            runQrReader()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data).contents?.let {
                viewModel.wallet.value?.address?.value = it
            }
        }
    }
}