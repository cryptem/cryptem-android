package io.cryptem.app.ui.poieditor.event

import io.cryptem.app.ui.poieditor.event.PoiEditorValidationEvent

class ValidationException(val type : Type) : Throwable() {

    enum class Type {
        ADDRESS_NOT_FOUND,
        UNSUPPORTED_COUNTRY
    }
}