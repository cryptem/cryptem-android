package io.cryptem.app.ui.about.event

import io.cryptem.app.model.ui.WalletCoin
import kodebase.event.LiveEvent

class ClipboardEvent(val coin : WalletCoin) : LiveEvent(){
}