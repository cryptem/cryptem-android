package io.cryptem.app.ui.map

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ui.base.BaseVM
import java.lang.reflect.Constructor
import javax.inject.Inject

@HiltViewModel
class MapVM @Inject constructor (val locationClient : FusedLocationProviderClient) : BaseVM() {

    val location = MutableLiveData<Location>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){

    }

    fun getLastLocation(){
        locationClient.lastLocation.addOnCompleteListener {
            location.value = it.result
        }
    }
}