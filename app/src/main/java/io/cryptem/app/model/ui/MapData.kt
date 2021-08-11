package io.cryptem.app.model.ui

class MapData(allCategories : List<PoiCategory>, val pois : List<Poi>) {

    val categories = ArrayList<PoiCategory>()
    private val categoriesMap = HashMap<String, PoiCategory>()
    private val poiMap = HashMap<PoiCategory, ArrayList<Poi>>()

    init {
        allCategories.forEach {
            categoriesMap[it.id] = it
        }
        pois.forEach { poi ->
            categoriesMap[poi.category?.id]?.let { category ->
                val poiList = poiMap[category] ?: ArrayList()
                poiList.add(poi)
                poiMap[category] = poiList
            }
        }
        categories.addAll(poiMap.keys.sortedByDescending { poiMap[it]?.size })
    }

    fun getPois(category: PoiCategory?) : List<Poi>{
        return if (category != null) {
            poiMap[category] ?: listOf()
        } else {
            pois
        }
    }

    fun search(search: String, category : PoiCategory?): List<Poi> {
        return getPois(category).filter { it.name?.contains(search, true) ?: false }
    }
}