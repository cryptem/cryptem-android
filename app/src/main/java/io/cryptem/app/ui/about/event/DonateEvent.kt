package io.cryptem.app.ui.about.event

import io.cryptem.app.model.DonateAddress
import kodebase.event.LiveEvent

class DonateEvent(val donate : DonateAddress) : LiveEvent(){
}