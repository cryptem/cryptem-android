package io.cryptem.app.ui.about.event

import io.cryptem.app.model.DonateAddress
import io.cryptem.app.model.ui.WalletCoin
import kodebase.event.LiveEvent

class DonateEvent(val donate : DonateAddress) : LiveEvent(){
}