package io.cryptem.app.ui.map.reportpoi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.DialogReportPoiBinding
import io.cryptem.app.ui.map.reportpoi.event.OkEvent
import kodebase.view.KodebaseDialogFragment

@AndroidEntryPoint
class ReportPoiDialog : KodebaseDialogFragment<ReportPoiDialogVM, DialogReportPoiBinding>(R.layout.dialog_report_poi) {
    override val viewModel: ReportPoiDialogVM by viewModels()
    val args : ReportPoiDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.poi.value = args.poi
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe(OkEvent::class){
            Toast.makeText(requireContext(), R.string.poi_report_ok, Toast.LENGTH_LONG).show()
            dismiss()
        }
        subscribe(OkEvent::class){
            Toast.makeText(requireContext(), R.string.poi_report_error, Toast.LENGTH_LONG).show()
        }
    }
}