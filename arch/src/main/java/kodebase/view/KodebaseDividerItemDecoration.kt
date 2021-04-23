package kodebase.view

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration

class KodebaseDividerItemDecoration(dividerDrawable : Drawable, orientation : Int, context : Context) : DividerItemDecoration(context, orientation) {

    init {
        setDrawable(dividerDrawable)
    }
}