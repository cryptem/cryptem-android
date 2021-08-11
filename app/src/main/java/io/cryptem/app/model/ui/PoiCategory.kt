package io.cryptem.app.model.ui

import android.os.Parcelable
import io.cryptem.app.R
import kotlinx.parcelize.Parcelize

@Parcelize
class PoiCategory(val id: String, val name : String) : Parcelable {

    override fun toString(): String {
        return name
    }

    fun getIcon() : Int {
        return when(id){
            "parallel_polis" -> R.drawable.ic_poi_parallel_polis
            "restaurant" -> R.drawable.ic_poi_restaurant
            "cafe" -> R.drawable.ic_poi_cafe
            "car_repair" -> R.drawable.ic_poi_car_repair
            "grocery" -> R.drawable.ic_poi_grocery
            "store" -> R.drawable.ic_poi_store
            "atm" -> R.drawable.ic_poi_atm
            "gym" -> R.drawable.ic_poi_gym
            "accommodation" -> R.drawable.ic_poi_accommodation
            "barber" -> R.drawable.ic_poi_barber
            "pub" -> R.drawable.ic_poi_pub
            "wine" -> R.drawable.ic_poi_wine
            "theater" -> R.drawable.ic_poi_theater
            "taxi" -> R.drawable.ic_poi_taxi
            "nightlife" -> R.drawable.ic_poi_nightlife
            "home_service" -> R.drawable.ic_poi_home_service
            "craftsman" -> R.drawable.ic_poi_craftsman
            "drugstore" -> R.drawable.ic_poi_drugstore
            "pharmacy" -> R.drawable.ic_poi_pharmacy
            "coworking" -> R.drawable.ic_poi_coworking
            "wellness" -> R.drawable.ic_poi_wellness
            "clothing" -> R.drawable.ic_poi_clothing
            "sport" -> R.drawable.ic_poi_sport
            "gas_station" -> R.drawable.ic_poi_gas_station
            "other" -> R.drawable.ic_poi_other
            else -> R.drawable.ic_poi_other
        }
    }
}

