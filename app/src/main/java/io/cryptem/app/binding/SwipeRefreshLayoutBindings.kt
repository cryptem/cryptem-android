package io.cryptem.app.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("refreshing")
fun SwipeRefreshLayout.bindRefresh(refreshing: Boolean) {
    isRefreshing = refreshing
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.bindRefresh(onRefresh : View.OnClickListener) {
    setOnRefreshListener {
        onRefresh.onClick(this)
    }
}