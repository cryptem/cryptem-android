package io.cryptem.app.model

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreRepository @Inject constructor() {

    val db = Firebase.firestore

    suspend fun addPoi(poi: Poi) = suspendCoroutine<DocumentReference> { cont ->
        val data = hashMapOf(
            "name" to poi.name,
            "url" to poi.url,
            "address" to poi.address,
            "country" to poi.country,
            "geo" to GeoPoint(poi.latitude, poi.longitude),
            "category" to poi.category
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

    suspend fun getAllPoi(country : String) = suspendCoroutine<List<Poi>> { cont ->
        db.collection("poi").whereEqualTo("country", country).get().addOnSuccessListener {
            cont.resumeWith(Result.success(it.documents.map {
                Poi(name = it.getString("name"),
                    url = it.getString("url"),
                    address = it.getString("address"),
                    country = it.getString("country"),
                    latitude = it.getGeoPoint("geo")?.latitude ?: 0.0,
                    longitude = it.getGeoPoint("geo")?.longitude ?: 0.0,
                    category = it.getString("category")
                    )
            }))
        }.addOnFailureListener {
            cont.resumeWithException(it)
        }
    }

    suspend fun getCategories() = suspendCoroutine<List<PoiCategory>> { cont ->
        db.collection("categories").get().addOnSuccessListener {
            cont.resumeWith(Result.success(it.documents.map {
                val name = it.getString("name_${Locale.getDefault().language}") ?: it.getString("name_en")
                PoiCategory(it.id, name ?: "Error") }))
        }.addOnFailureListener {
            cont.resumeWithException(it)
        }
    }
}