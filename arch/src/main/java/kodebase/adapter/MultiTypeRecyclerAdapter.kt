package kodebase.adapter

import androidx.databinding.ObservableArrayList
import kodebase.viewmodel.KodebaseViewModel

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */


class MultiTypeRecyclerAdapter(items: ObservableArrayList<Any>, var strategy: RecyclerLayoutStrategy, vm: KodebaseViewModel?) :
    KodebaseRecyclerAdapter<Any>(items, vm) {

    override fun getLayoutId(itemType: Int): Int {
        return itemType
    }

    override fun getItemViewType(position: Int): Int {
        return strategy.getLayoutId(getItem(position))
    }
}
