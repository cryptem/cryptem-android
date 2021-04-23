package kodebase.binding

import android.content.Context
import android.view.inputmethod.InputMethodManager
import kodebase.adapter.BaseSpinnerAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

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
    setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

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
