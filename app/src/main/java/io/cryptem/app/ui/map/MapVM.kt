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
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MapVM @Inject constructor (val locationClient : FusedLocationProviderClient, val firestoreRepository: FirestoreRepository, val prefs : SharedPrefsRepository, val remoteConfigRepository: RemoteConfigRepository) : BaseVM() {

    val location = MutableLiveData<Location?>()
    val categoriesList = ObservableArrayList<PoiCategory>()
    val categories = MutableLiveData<List<PoiCategory>>()
    val pois = MutableLiveData<List<Poi>>()
    val countries = ArrayList<String>()
    val country = SafeMutableLiveData(prefs.getCountry())
    var countryInitFlag = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        countries.clear()
        countries.addAll(remoteConfigRepository.getSupportedCountries())
        if (!countries.contains(Locale.getDefault().country)){
            countries.add(0, Locale.getDefault().country)
        }
        country.observeForever {
            if (countryInitFlag) {
                prefs.saveCountry(it)
                loadCategories()
            } else {
                countryInitFlag = true
            }
        }
    }

    fun loadCategories(){
        categoriesList.clear()
        viewModelScope.launch {
            kotlin.runCatching {
                firestoreRepository.getCategories()
            }.onSuccess {
                categories.value = it
                categoriesList.addAll(it)
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun loadPois(){
        viewModelScope.launch {
            kotlin.runCatching {
                firestoreRepository.getAllPoi(country.value)
            }.onSuccess {
                pois.value = it
            }.onFailure {
                L.e(it)
            }
        }
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
}