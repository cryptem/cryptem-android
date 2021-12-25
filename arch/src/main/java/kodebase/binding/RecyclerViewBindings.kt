package kodebase.binding

import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kodebase.adapter.KodebaseRecyclerAdapter
import kodebase.adapter.MultiTypeRecyclerAdapter
import kodebase.adapter.RecyclerLayoutStrategy
import kodebase.adapter.SingleTypeRecyclerAdapter
import kodebase.view.KodebaseDividerItemDecoration
import kodebase.viewmodel.KodebaseViewModel

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@BindingAdapter(
    value = ["viewModel", "data", "layoutId", "layoutStrategy", "lifecycle", "parentItem"],
    requireAll = false
)
fun <T> RecyclerView.bindItems(
    vm: KodebaseViewModel?,
    data: ObservableArrayList<T>,
    layoutId: Int?,
    layoutStrategy: RecyclerLayoutStrategy?,
    lifecycleOwner: LifecycleOwner?,
    parentItem: Any?,
) {
    if (adapter == null) {
        if (layoutStrategy == null) {
            if (layoutId != null) {
                adapter = SingleTypeRecyclerAdapter(data, vm, layoutId)
            }
        } else {
            adapter = MultiTypeRecyclerAdapter(data as ObservableArrayList<Any>, layoutStrategy, vm)
        }
    } else {
        (adapter as KodebaseRecyclerAdapter<T>).setItems(data)
    }
    (adapter as KodebaseRecyclerAdapter<*>).lifecycleOwner = lifecycleOwner
    (adapter as KodebaseRecyclerAdapter<*>).parentItem = parentItem
}

@BindingAdapter(value = ["divider"], requireAll = false)
fun RecyclerView.setDivider(divider : Drawable){
    layoutManager?.let {
        val orientation = if (it is LinearLayoutManager){
            it.orientation
        } else {
            LinearLayout.VERTICAL
        }
        addItemDecoration(KodebaseDividerItemDecoration(divider, orientation, context))
    }
}
