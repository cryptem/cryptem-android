package kodebase.binding

import android.content.res.ColorStateList
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */


@BindingAdapter(value = ["imageResource"], requireAll = false)
fun ImageView.bindImageResource(@DrawableRes resId: Int) {
    setImageResource(resId)
}

@BindingAdapter(value = ["url"], requireAll = false)
fun ImageView.bindUrl(url: String?) {
    Glide.with(context).load(url).into(this)
}

@BindingAdapter(value = ["uri"], requireAll = false)
fun ImageView.bindUri(uri: Uri?) {
    Glide.with(context).load(uri).into(this)
}

@BindingAdapter(value = ["file"], requireAll = false)
fun ImageView.bindFile(file: File?) {
    Glide.with(context).load(file).into(this)
}

@BindingAdapter("tintResource")
fun ImageView.bindTintResource(resId: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ResourcesCompat.getColor(context.resources, resId, null)))
}


