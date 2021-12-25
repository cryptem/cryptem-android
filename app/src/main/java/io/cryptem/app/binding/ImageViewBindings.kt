package io.cryptem.app.binding

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import io.cryptem.app.util.svg.GlideApp
import io.cryptem.app.util.svg.SvgSoftwareLayerSetter

@BindingAdapter(value = ["svgUrl", "skipCache"], requireAll = false)
fun ImageView.bindSvg(url: String?, skipCache : Boolean = false) {
    if (url != null) {
        val requestBuilder = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .listener(SvgSoftwareLayerSetter())

        requestBuilder.load(url).diskCacheStrategy(DiskCacheStrategy.NONE).into(this)
    }
}