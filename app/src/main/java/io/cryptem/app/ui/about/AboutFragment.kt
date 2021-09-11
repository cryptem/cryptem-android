package io.cryptem.app.ui.about

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentAboutBinding
import io.cryptem.app.ui.about.event.ClipboardEvent
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UrlEvent

@AndroidEntryPoint
class AboutFragment : BaseFragment<AboutVM, FragmentAboutBinding>(R.layout.fragment_about) {
    override val viewModel: AboutVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(ClipboardEvent::class){
            Toast.makeText(requireContext(), getString(R.string.about_copied, it.coin.title), Toast.LENGTH_LONG).show()
        }

        observe(UrlEvent::class){
            showUrl(it.url)
        }
    }
}