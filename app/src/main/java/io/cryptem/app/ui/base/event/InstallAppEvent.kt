package io.cryptem.app.ui.base.event

import kodebase.event.LiveEvent

class InstallAppEvent(val packageName: String) : LiveEvent() {
}