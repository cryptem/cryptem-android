package io.cryptem.app.ui.trezor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentTrezorBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UrlEvent

@AndroidEntryPoint
class TrezorFragment : BaseFragment<TrezorVM, FragmentTrezorBinding>(R.layout.fragment_trezor) {
    override val viewModel: TrezorVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(UrlEvent::class){
            showUrl(it.url)
        }
    }
}