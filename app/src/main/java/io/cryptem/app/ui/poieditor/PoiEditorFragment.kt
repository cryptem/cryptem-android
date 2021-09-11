package io.cryptem.app.ui.poieditor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPoiEditorBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.poieditor.event.GoogleMapsEvent
import io.cryptem.app.ui.poieditor.event.PoiEditorValidationEvent
import io.cryptem.app.ui.poieditor.event.PoiSentEvent
import io.cryptem.app.ui.poieditor.event.ValidationException
import java.util.*

@AndroidEntryPoint
class PoiEditorFragment : BaseFragment<PoiEditorVM, FragmentPoiEditorBinding>(R.layout.fragment_poi_editor) {
    override val viewModel: PoiEditorVM by viewModels()
    val args : PoiEditorFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.name.value = args.name
        viewModel.url.value = args.url

        observe(GoogleMapsEvent::class){
            runOrInstall("com.google.android.apps.maps")
        }

        observe(PoiEditorValidationEvent::class){
            when(it.type){
                ValidationException.Type.ADDRESS_NOT_FOUND -> showAddressNotFound()
                ValidationException.Type.UNSUPPORTED_COUNTRY -> showUnsupportedCountryDialog()
            }
        }

        observe(PoiSentEvent::class){
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.poi_editor_success_title)
                .setMessage(R.string.poi_editor_success_message)
                .setPositiveButton(R.string.ok){ _, _ -> }
                .setOnDismissListener{ navController().navigateUp() }.show()
        }
    }

    fun showUnsupportedCountryDialog(){
        viewModel.country.value?.let {
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.map_unsupported_country_title)
                .setMessage(getString(R.string.map_unsupported_country_message,  Locale(Locale.getDefault().language, it).displayCountry))
                .setPositiveButton(R.string.ok){ _, _ -> }
                .setNegativeButton(R.string.donate){ _, _ -> navigate(R.id.aboutFragment)}.show()
        }
    }

    fun showAddressNotFound(){
        MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.poi_editor_address_not_found_title)
            .setMessage(R.string.poi_editor_address_not_found_message)
            .setPositiveButton(R.string.ok){ _, _ -> }.show()
    }

}