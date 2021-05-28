package io.cryptem.app.ui.map.poi

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.DialogPoiBinding
import kodebase.view.KodebaseDialogFragment

@AndroidEntryPoint
class PoiDialog : KodebaseDialogFragment<PoiDialogVM, DialogPoiBinding>(R.layout.dialog_poi) {
    override val viewModel: PoiDialogVM by viewModels()
    val args : PoiDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.poi.value = args.poi
    }
}