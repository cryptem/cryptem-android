package io.cryptem.app.binding

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.Key
import com.bumptech.glide.signature.ObjectKey
import io.cryptem.app.util.svg.GlideApp
import io.cryptem.app.util.svg.SvgSoftwareLayerSetter
import java.security.Signature

@BindingAdapter(value = ["svgUrl", "skipCache"], requireAll = false)
fun ImageView.bindSvg(url: String?, skipCache : Boolean = false) {
    if (url != null) {
        var requestBuilder = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .listener(SvgSoftwareLayerSetter())

        if (skipCache){
            requestBuilder = requestBuilder.skipMemoryCache(skipCache).signature(ObjectKey(System.currentTimeMillis()))
        }
        requestBuilder.load(url).into(this)
    }
}