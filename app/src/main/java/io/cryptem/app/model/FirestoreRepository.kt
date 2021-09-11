package io.cryptem.app.model

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.cryptem.app.model.ui.MapData
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import io.cryptem.app.model.ui.PoiReport
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreRepository @Inject constructor() {

    val db = Firebase.firestore
    var categoriesCache : List<PoiCategory>? = null
    var categoriesMap = HashMap<String, PoiCategory>()

    var mapDataCache = HashMap<String, MapData>()

    suspend fun addPoi(poi: Poi) = suspendCoroutine<DocumentReference> { cont ->
        val data = hashMapOf(
            "name" to poi.name,
            "url" to poi.url,
            "address" to poi.address,
            "country" to poi.country,
            "geo" to GeoPoint(poi.latitude, poi.longitude),
            "category" to poi.category?.id,
            "approved" to false
        )

        db.collection("poi")
            .add(data)
            .addOnSuccessListener {
                cont.resumeWith(Result.success(it))
            }
            .addOnFailureListener {
                cont.resumeWithException(it)
            }
    }

    private suspend fun getAllPoi(country : String) = suspendCoroutine<List<Poi>> { cont ->
        db.collection("poi").whereEqualTo("country", country).whereEqualTo("approved", true).get().addOnSuccessListener {
            cont.resumeWith(Result.success(it.documents.map {
                Poi(id = it.id,
                    name = it.getString("name"),
                    url = it.getString("url"),
                    address = it.getString("address"),
                    country = it.getString("country"),
                    latitude = it.getGeoPoint("geo")?.latitude ?: 0.0,
                    longitude = it.getGeoPoint("geo")?.longitude ?: 0.0,
                    category = categoriesMap[it.getString("category")]
                    )
            }))
        }.addOnFailureListener {
            cont.resumeWithException(it)
        }
    }

    suspend fun getCategories() = suspendCoroutine<List<PoiCategory>> { cont ->
        if (categoriesCache == null){
            db.collection("categories").get().addOnSuccessListener {
                categoriesCache = it.documents.map {
                    val name = it.getString("name_${Locale.getDefault().language}") ?: it.getString("name_en")
                    val result = PoiCategory(it.id, name ?: "Error")
                    categoriesMap[result.id] = result
                    result}
                    cont.resumeWith(Result.success(categoriesCache.orEmpty()))
            }.addOnFailureListener {
                cont.resumeWithException(it)
            }
        } else {
            cont.resumeWith(Result.success(categoriesCache.orEmpty()))
        }
    }

    suspend fun reportPoi(report: PoiReport) = suspendCoroutine<DocumentReference> { cont ->
        val data = hashMapOf(
            "poi_id" to report.poiId,
            "type" to report.type.id,
            "note" to report.note
        )

        db.collection("poi_reports").add(data)
            .addOnSuccessListener {
                cont.resumeWith(Result.success(it))
            }
            .addOnFailureListener {
                cont.resumeWithException(it)
            }
    }

    suspend fun getMapData(country : String) : MapData?{
        if (!mapDataCache.contains(country)){
            mapDataCache[country] = MapData(categoriesCache ?: getCategories(), getAllPoi(country))
        }
        return mapDataCache[country]
    }

    fun clearMapDataCache(country : String){
        mapDataCache.remove(country)
    }
}