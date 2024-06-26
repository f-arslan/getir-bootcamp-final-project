package com.patika.getir_lite.util.decor

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

/**
 * The dividers are not drawn for the last two items or any item that is currently in a transition state
 * (e.g., during animations).
 *
 * @property divider The drawable used as a divider.
 * @property margin The margin on both sides of the divider (left and right).
 */
class DividerDecoration(private val divider: Drawable, private val margin: Int) :
    RecyclerView.ItemDecoration() {
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + margin
        val right = parent.width - parent.paddingRight - margin

        val childCount = parent.childCount
        for (i in 0 until childCount - 2) {
            val child = parent.getChildAt(i)

            // Skip drawing for children in transition
            if (child.translationX != 0f || child.translationY != 0f || child.alpha != 1f) {
                continue
            }

            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}
