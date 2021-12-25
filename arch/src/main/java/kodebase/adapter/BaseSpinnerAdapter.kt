package kodebase.adapter

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import kodebase.R

class BaseSpinnerAdapter<T>(
    context: Context,
    @LayoutRes val layoutId: Int?,
    var data: ArrayList<T>
) : ArrayAdapter<T>(context, layoutId ?: R.layout.support_simple_spinner_dropdown_item, data) {

    fun indexOf(item: Any?) : Int{
        return data.indexOf(item)
    }
}