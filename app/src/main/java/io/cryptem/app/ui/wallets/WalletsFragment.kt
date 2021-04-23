package io.cryptem.app.ui.wallets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentWalletsBinding
import io.cryptem.app.ui.base.BaseFragment


@AndroidEntryPoint
class WalletsFragment : BaseFragment<WalletsVM, FragmentWalletsBinding>(R.layout.fragment_wallets) {
    override val viewModel: WalletsVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.itemAnimator = null
    }
}