package com.threes.scenespotinseoul.utilities

import android.graphics.Rect
import android.support.annotation.IntDef
import android.support.annotation.StringDef
import android.support.v7.widget.RecyclerView
import android.view.View

const val DIR_LEFT = "dir_left"
const val DIR_TOP = "dir_top"
const val DIR_RIGHT = "dir_right"
const val DIR_BOTTOM = "dir_bottom"

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    DIR_LEFT,
    DIR_TOP,
    DIR_RIGHT,
    DIR_BOTTOM
)
annotation class Direction

const val OFFSET_SMALL = 8
const val OFFSET_NORMAL = 16

@IntDef(
    OFFSET_SMALL,
    OFFSET_NORMAL
)
annotation class Offset

class ItemOffsetDecoration(@Direction private val direction: String, @Offset private val itemOffSet: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        when (direction) {
            DIR_LEFT -> outRect.set(itemOffSet, 0, 0, 0)
            DIR_TOP -> outRect.set(0, itemOffSet, 0, 0)
            DIR_RIGHT -> outRect.set(0, 0, itemOffSet, 0)
            DIR_BOTTOM -> outRect.set(0, 0, 0, itemOffSet)
        }
    }
}