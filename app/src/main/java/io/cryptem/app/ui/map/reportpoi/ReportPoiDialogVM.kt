package io.cryptem.app.ui.map.reportpoi

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.FirestoreRepository
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiReport
import io.cryptem.app.model.ui.PoiReportType
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.map.reportpoi.event.ErrorEvent
import io.cryptem.app.ui.map.reportpoi.event.OkEvent
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportPoiDialogVM @Inject constructor(val firestoreRepository: FirestoreRepository) : BaseVM() {
    val poi = MutableLiveData<Poi>()
    val types = ObservableArrayList<PoiReportType>().apply {
        addAll(listOf(PoiReportType.WRONG_LOCATION, PoiReportType.DISCONTINUED, PoiReportType.NOT_CRYPTO, PoiReportType.OTHER))
    }
    val selectedType = SafeMutableLiveData(PoiReportType.WRONG_LOCATION)
    val note = SafeMutableLiveData("")
    val loading = MutableLiveData(false)

    fun send(){
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                poi.value?.id?.let {
                    firestoreRepository.reportPoi(PoiReport(it, selectedType.value, note.value))
                }
            }.onSuccess {
                loading.value = false
                publish(OkEvent())
            }.onFailure {
                loading.value = false
                publish(ErrorEvent(it))
            }
        }

    }
}