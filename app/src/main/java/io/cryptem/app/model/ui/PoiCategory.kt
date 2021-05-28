package io.cryptem.app.model.ui

import io.cryptem.app.R

class PoiCategory(val id: String, val name : String) {

    override fun toString(): String {
        return name
    }

    fun getIcon() : Int {
        return when(id){
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
            "bar" -> R.drawable.ic_poi_bar
            "wine" -> R.drawable.ic_poi_wine
            "theater" -> R.drawable.ic_poi_theater
            "taxi" -> R.drawable.ic_poi_taxi
            "nightlife" -> R.drawable.ic_poi_nightlife
            "home_service" -> R.drawable.ic_poi_home_service
            "bookstore" -> R.drawable.ic_poi_bookstore
            "cleaning_service" -> R.drawable.ic_poi_cleaning_service
            "drugstore" -> R.drawable.ic_poi_drugstore
            "pharmacy" -> R.drawable.ic_poi_pharmacy
            "plumber" -> R.drawable.ic_poi_plumber
            "painter" -> R.drawable.ic_poi_painter
            "coworking" -> R.drawable.ic_poi_coworking
            "other" -> R.drawable.ic_poi_other
            else -> R.drawable.ic_poi_other
        }
    }
}

