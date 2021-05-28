package io.cryptem.app.binding

import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import kodebase.adapter.BaseSpinnerAdapter

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@Suppress("UNCHECKED_CAST")
@BindingAdapter(
    value = ["data", "layoutId"],
    requireAll = false
)
fun <T> AutoCompleteTextView.bindItems(
    data: ArrayList<T>,
    layoutId: Int?
) {
    if (adapter == null) {
        setAdapter(BaseSpinnerAdapter(context, layoutId, data))
    } else {
        (adapter as BaseSpinnerAdapter<T>).data = data
    }
}

@BindingAdapter("selectedItem")
fun AutoCompleteTextView.setSelectedItem(newValue: Any?) {
    if (tag != newValue) {
        tag = newValue
        setText(newValue?.toString(), false)
    }
}

@InverseBindingAdapter(attribute = "selectedItem")
fun AutoCompleteTextView.getSelectedItem(): Any? {
    return tag
}

@BindingAdapter("selectedItemAttrChanged")
fun AutoCompleteTextView.bindSelectedItemListener(
    attrChange: InverseBindingListener
) {
    setOnItemClickListener { parent, view, position, id ->
        tag = parent.getItemAtPosition(position)
        attrChange.onChange()
    }
}
