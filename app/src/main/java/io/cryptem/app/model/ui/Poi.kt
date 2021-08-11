package io.cryptem.app.model.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Poi(
    val id: String? = null,
    val name: String?,
    val url: String?,
    val address: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val category: PoiCategory?
)  : Parcelable{

}