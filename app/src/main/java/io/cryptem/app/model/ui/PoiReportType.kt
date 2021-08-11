package io.cryptem.app.model.ui

import io.cryptem.app.App
import io.cryptem.app.R

enum class PoiReportType(val id : String, val title : Int) {
    DISCONTINUED("discontinued", R.string.report_poi_type_discontinued),
    WRONG_LOCATION("wrong_location", R.string.report_poi_type_wrong_location),
    NOT_CRYPTO("not_crypto", R.string.report_poi_type_not_accepting_crypto),
    OTHER("other", R.string.report_poi_type_other);

    override fun toString(): String {
        return App.instance.getString(title)
    }
}