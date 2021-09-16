package io.cryptem.app.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentAboutBinding
import io.cryptem.app.model.DonateAddress
import io.cryptem.app.model.ui.SoftwareWallet
import io.cryptem.app.ui.about.event.DonateEvent
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UrlEvent

@AndroidEntryPoint
class AboutFragment : BaseFragment<AboutVM, FragmentAboutBinding>(R.layout.fragment_about) {
    override val viewModel: AboutVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(DonateEvent::class) {
            handleDonateEvent(it.donate)
        }
        observe(UrlEvent::class) {
            showUrl(it.url)
        }
    }

    private fun handleDonateEvent(donate: DonateAddress) {
        Toast.makeText(
            requireContext(),
            getString(R.string.about_copied, donate.coin.title),
            Toast.LENGTH_LONG
        ).show()

        try {
            runApp(SoftwareWallet.EXODUS.packageName)
        } catch (t: Throwable) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("${donate.coin.id}:${donate.address}")
                    )
                )
            } catch (t: Throwable) {
                // Ignore
            }
        }
    }
}