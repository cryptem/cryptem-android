package kodebase.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kodebase.adapter.KodebaseRecyclerAdapter
import kodebase.adapter.SingleTypeRecyclerAdapter
import kodebase.viewmodel.KodebaseViewModel

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@BindingAdapter(
    value = ["viewModel", "items", "layoutId", "tabLayout"],
    requireAll = false
)
fun <T> ViewPager2.bindItems(
    vm: KodebaseViewModel?,
    items: ObservableArrayList<T>,
    layoutId: Int?,
    tabLayout: TabLayout?
) {
    if (adapter == null) {
        if (layoutId != null) {
            adapter = SingleTypeRecyclerAdapter(items, vm, layoutId)
        }
        if (tabLayout != null) {
            // Use TabLayout as ViewPager2 indicator
            TabLayoutMediator(tabLayout, this) { _, _ -> }.attach()
        }
    } else {
        (adapter as KodebaseRecyclerAdapter<T>).setItems(items)
    }
}