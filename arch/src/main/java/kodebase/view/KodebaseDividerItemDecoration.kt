package kodebase.view

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.DividerItemDecoration

class KodebaseDividerItemDecoration(dividerDrawable : Drawable, orientation : Int, context : Context) : DividerItemDecoration(context, orientation) {

    init {
        setDrawable(dividerDrawable)
    }
}