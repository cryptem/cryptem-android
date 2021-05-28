package io.cryptem.app.ui.pay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPayBinding
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.InstallAppEvent
import io.cryptem.app.ui.base.event.RunAppEvent


@AndroidEntryPoint
class PayFragment : BaseFragment<PayVM, FragmentPayBinding>(R.layout.fragment_pay){
    override val viewModel: PayVM by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(RunAppEvent::class){
            runApp(it.packageName)
        }
        observe(InstallAppEvent::class){
            installApp(it.packageName)
        }
        runApp(viewModel.defaultWallet.value)
    }


}