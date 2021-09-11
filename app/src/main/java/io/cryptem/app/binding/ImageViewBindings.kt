package io.cryptem.app.binding

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import io.cryptem.app.util.svg.GlideApp
import io.cryptem.app.util.svg.SvgSoftwareLayerSetter

@BindingAdapter("svgUrl")
fun ImageView.bindSvg(url: String?) {
    if (url != null) {
        val requestBuilder = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .listener(SvgSoftwareLayerSetter())
        requestBuilder.load(url).into(this)
    }
}