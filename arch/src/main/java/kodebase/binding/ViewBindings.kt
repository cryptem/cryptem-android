package kodebase.binding

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@BindingAdapter("backgroundResource")
fun View.setBackgroundResource(resId: Int) {
    if (resId != 0) {
        setBackgroundResource(resId)
    } else {
        background = null
    }
}

@BindingAdapter(
    value = ["visibleOrGone", "animationIn", "animationOut", "animationInOffset", "animationOutOffset"],
    requireAll = false
)
fun View.setVisibleOrGone(
    visible: Boolean,
    animationIn: Animation?,
    animationOut: Animation?,
    animationInOffset: Long?,
    animationOutOffset: Long?
) {
    val newVisibility = if (visible) View.VISIBLE else View.GONE
    if (visibility != newVisibility) {
        visibility = newVisibility

        if (newVisibility == View.VISIBLE) {
            if (animationIn != null) {
                startAnimation(
                    animationIn.apply {
                        startOffset = animationInOffset ?: 0L
                    })
            }
        }
        if (newVisibility == View.GONE) {
            if (animationOut != null) {
                startAnimation(
                    animationOut.apply {
                        startOffset = animationOutOffset ?: 0L
                    })
            }
        }
    }
}

@BindingAdapter(value = ["visibleOrInvisible","animationIn", "animationOut", "animationInOffset", "animationOutOffset"], requireAll = false)
fun View.setVisibleOrInvisible(visible: Boolean,
                               animationIn: Animation?,
                               animationOut: Animation?,
                               animationInOffset: Long?,
                               animationOutOffset: Long?) {
    val newVisibility = if (visible) View.VISIBLE else View.INVISIBLE
    if (visibility != newVisibility) {
        visibility = newVisibility

        if (newVisibility == View.VISIBLE) {
            if (animationIn != null) {
                startAnimation(
                    animationIn.apply {
                        startOffset = animationInOffset ?: 0L
                    })
            }
        }
        if (newVisibility == View.INVISIBLE) {
            if (animationOut != null) {
                startAnimation(
                    animationOut.apply {
                        startOffset = animationOutOffset ?: 0L
                    })
            }
        }
    }
}

@BindingAdapter("onClickUrl")
fun View.setOnclickUrl(url: String?) {
    setOnClickListener {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (t : Throwable){
            Log.d("View.onClickUrl", t.message ?: t.toString())
        }
    }
}

