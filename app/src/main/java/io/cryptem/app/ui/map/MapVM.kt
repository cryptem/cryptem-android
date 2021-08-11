package io.cryptem.app.ui.map

import android.annotation.SuppressLint
import android.location.Location
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.FirestoreRepository
import io.cryptem.app.model.HomeScreen
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.ui.MapData
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class MapVM @Inject constructor (val locationClient : FusedLocationProviderClient, val firestoreRepository: FirestoreRepository, val prefs : SharedPrefsRepository, val remoteConfigRepository: RemoteConfigRepository) : BaseVM() {

    val location = MutableLiveData<Location?>()
    val categories = ObservableArrayList<PoiCategory>()
    val pois = MutableLiveData<List<Poi>>()
    val selectedCategory = MutableLiveData<PoiCategory?>()

    val selectedPoi = MutableLiveData<Poi?>()

    val countries = ArrayList<String>()
    val country = SafeMutableLiveData(prefs.getCountry())
    var countryInitFlag = false
    val data = MutableLiveData<MapData>()

    val search = MutableLiveData<String?>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        //prefs.setHomeScreen(HomeScreen.MAP)

        countries.clear()
        countries.addAll(remoteConfigRepository.getSupportedCountries())
        if (!countries.contains(Locale.getDefault().country)){
            countries.add(0, Locale.getDefault().country)
        }
        country.observeForever {
            if (countryInitFlag) {
                prefs.saveCountry(it)
                loadData()
            } else {
                countryInitFlag = true
            }
        }
        search.observeForever {
            it?.let {
                pois.value = data.value?.search(it, selectedCategory.value)
            }
        }
    }

    fun loadData(){
        categories.clear()
        viewModelScope.launch {
            kotlin.runCatching {
                firestoreRepository.getMapData(country.value)
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
    }

    fun applyFilter(category : PoiCategory){
        if (selectedCategory.value == category){
            selectedCategory.value = null
        } else {
            selectedCategory.value = category
        }
        pois.value = data.value?.getPois( selectedCategory.value)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(){
        locationClient.lastLocation.addOnCompleteListener {
            location.value = it.result
        }
    }

    fun isCountrySupported() : Boolean{
        return remoteConfigRepository.getSupportedCountries().contains(prefs.getCountry())
    }

    fun getCountry() : String{
        return prefs.getCountry()
    }

    fun showInGmaps(poi : Poi){
        poi.url?.let {
            publish(UrlEvent(poi.url))
        }
    }

    fun report(poi: Poi){
        navigate(MapFragmentDirections.actionMapFragmentToReportPoiDialog(poi))
    }
}