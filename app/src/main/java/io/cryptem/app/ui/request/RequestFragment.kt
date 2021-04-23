package io.cryptem.app.ui.request

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentRequestBinding
import io.cryptem.app.ui.base.BaseFragment


@AndroidEntryPoint
class RequestFragment : BaseFragment<RequestVM, FragmentRequestBinding>(R.layout.fragment_request){
    override val viewModel: RequestVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerWallets.itemAnimator = null

        viewModel.qr.observe(viewLifecycleOwner){
            showQR(it)
        }

        if (viewModel.hasDefaultWallet()) {
            showKeyboard(binding.inputAmount)
        }
    }

    private fun showQR(text: String){
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 256, 256)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.imageQR.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.request, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.walletsFragment -> navigate(RequestFragmentDirections.actionRequestFragmentToWalletsFragment())
        }
        return super.onOptionsItemSelected(item)
    }

}