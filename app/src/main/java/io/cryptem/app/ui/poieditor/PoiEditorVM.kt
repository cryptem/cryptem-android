package io.cryptem.app.ui.poieditor

import android.location.Geocoder
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.FirestoreRepository
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.poieditor.event.GoogleMapsEvent
import io.cryptem.app.ui.poieditor.event.PoiEditorValidationEvent
import io.cryptem.app.ui.poieditor.event.PoiSentEvent
import io.cryptem.app.ui.poieditor.event.ValidationException
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoiEditorVM @Inject constructor(
    val geocoder: Geocoder,
    val firestoreRepository: FirestoreRepository,
    val remoteConfigRepository: RemoteConfigRepository,
    val analytics: AnalyticsRepository
) : BaseVM() {

    val name = MutableLiveData<String?>()
    val url = MutableLiveData<String?>()
    val address = MutableLiveData<String?>()
    val latitude = SafeMutableLiveData(0.0)
    val longitude = SafeMutableLiveData(0.0)
    val country = MutableLiveData<String?>()
    val categories = ObservableArrayList<PoiCategory>()
    val category = MutableLiveData<PoiCategory?>()
    val loading = MutableLiveData(false)

    override fun onCreate(owner: LifecycleOwner) {
        loadCategories()
    }

    override fun onResume(owner: LifecycleOwner) {
        analytics.logPoiEditorScreen()
    }

    fun showGoogleMaps() {
        publish(GoogleMapsEvent())
    }

    fun getGps() {
        address.value?.let {
            geocoder.getFromLocationName(it, 1)?.firstOrNull()?.let {
                country.value = it.countryCode
                latitude.value = it.latitude
                longitude.value = it.longitude
            }
        }
    }

    fun loadCategories() {
        categories.clear()
        viewModelScope.launch {
            kotlin.runCatching {
                firestoreRepository.getCategories()
            }.onSuccess {
                categories.addAll(it.sortedBy { it.name })
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun send() {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                if(address.value?.length ?: 0 > 10) {
                    getGps()
                }
                if (latitude.value == 0.0 || longitude.value == 0.0 || country.value == null) {
                    throw ValidationException(ValidationException.Type.ADDRESS_NOT_FOUND)
                }
                if (!remoteConfigRepository.getSupportedCountries().contains(country.value)) {
                    throw ValidationException(ValidationException.Type.UNSUPPORTED_COUNTRY)
                }
                firestoreRepository.addPoi(
                    Poi(
                        name = name.value,
                        url = url.value,
                        address = address.value,
                        country = country.value,
                        latitude = latitude.value,
                        longitude = longitude.value,
                        category = category.value
                    )
                )
            }.onSuccess {
                loading.value = false
                publish(PoiSentEvent())
            }.onFailure {
                loading.value = false
                L.e(it)
                if (it is ValidationException) {
                    publish(PoiEditorValidationEvent(it.type))
                }
            }
        }
    }

}