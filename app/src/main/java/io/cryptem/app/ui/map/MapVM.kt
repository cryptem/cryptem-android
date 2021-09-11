package io.cryptem.app.ui.map

import android.annotation.SuppressLint
import android.location.Location
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.FirestoreRepository
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.ui.MapData
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.ui.map.event.InvalidateOptionsMenuEvent
import io.cryptem.app.ui.map.event.UnsupportedCountryEvent
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MapVM @Inject constructor(
    val locationClient: FusedLocationProviderClient,
    val firestoreRepository: FirestoreRepository,
    val prefs: SharedPrefsRepository,
    val remoteConfigRepository: RemoteConfigRepository,
    private val analytics : AnalyticsRepository
) : BaseVM() {

    val location = MutableLiveData<Location?>()
    val categories = ObservableArrayList<PoiCategory>()
    val pois = MutableLiveData<List<Poi>>()
    val selectedCategory = MutableLiveData<PoiCategory?>()
    val selectedPoi = MutableLiveData<Poi?>()
    val countries = ArrayList<String>()
    var previousCountry: String? = null
    val country = MutableLiveData<String>(null)
    val data = MutableLiveData<MapData>()
    val mapType = SafeMutableLiveData(prefs.getMapType())

    val search = MutableLiveData<String?>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        //prefs.setHomeScreen(HomeScreen.MAP)
        loadCountries()
        country.observeForever {
            if (previousCountry != it) {
                previousCountry = it
                prefs.saveCountry(it)
                loadData()
            }
        }
        search.observeForever {
            it?.let {
                if (data.value != null) {
                    pois.value = data.value?.search(it, selectedCategory.value)
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logMapScreen()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            kotlin.runCatching {
                remoteConfigRepository.getSupportedCountries()
            }.onSuccess {
                countries.clear()
                countries.addAll(it)
                country.value = prefs.getCountry()
                publish(InvalidateOptionsMenuEvent())
            }
        }
    }

    private fun loadData() {
        country.value?.let { country ->
            if (countries.contains(country)) {
                categories.clear()
                viewModelScope.launch {
                    kotlin.runCatching {
                        firestoreRepository.getMapData(country)
                    }.onSuccess {
                        it?.let {
                            data.value = it
                            categories.addAll(it.categories)
                            pois.value = it.pois
                        }
                    }.onFailure {
                        L.e(it)
                    }
                }
            } else {
                publish(UnsupportedCountryEvent())
            }
        }

    }

    fun applyFilter(category: PoiCategory) {
        if (selectedCategory.value == category) {
            selectedCategory.value = null
        } else {
            selectedCategory.value = category
        }
        pois.value = data.value?.getPois(selectedCategory.value)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        locationClient.lastLocation.addOnCompleteListener {
            location.value = it.result
        }
    }

    fun getCountry(): String {
        return prefs.getCountry()
    }

    fun showInGmaps(poi: Poi) {
        poi.url?.let {
            publish(UrlEvent(poi.url))
        }
    }

    fun report(poi: Poi) {
        navigate(MapFragmentDirections.actionMapFragmentToReportPoiDialog(poi))
    }

    fun toggleMapType(){
        if (mapType.value == GoogleMap.MAP_TYPE_NORMAL){
            mapType.value = GoogleMap.MAP_TYPE_HYBRID
        } else {
            mapType.value = GoogleMap.MAP_TYPE_NORMAL
        }
        prefs.saveMapType(mapType.value)
    }
}