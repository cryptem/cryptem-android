package io.cryptem.app.ui.map.poi

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.ui.base.BaseVM
import javax.inject.Inject

@HiltViewModel
class PoiDialogVM @Inject constructor() : BaseVM() {
    val poi = MutableLiveData<Poi>()
}