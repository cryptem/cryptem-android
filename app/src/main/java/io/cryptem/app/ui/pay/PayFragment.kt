package io.cryptem.app.ui.pay

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentPayBinding
import io.cryptem.app.ui.base.BaseFragment

@AndroidEntryPoint
class PayFragment : BaseFragment<PayVM, FragmentPayBinding>(R.layout.fragment_pay){
    override val viewModel: PayVM by viewModels()

}