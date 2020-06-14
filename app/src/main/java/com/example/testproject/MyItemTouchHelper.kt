package com.example.testproject

import android.graphics.Canvas
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.max

private const val MIN_CARD_SCALE = 0.7f
private const val FADE_ANIMATION_DURATION = 150L

class MyItemTouchHelper : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    0
) {
    var drag = false
    var removeFlag = false
    private var toRemove : RecyclerView.ViewHolder? = null
    private var selected : RecyclerView.ViewHolder? = null

    val onDragListener = mutableListOf<(Boolean)->Unit>()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = true

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        if(toRemove != null && removeFlag) {
            (recyclerView.adapter as PetsRecyclerAdapter).removeItem(toRemove!!)
            removeFlag = false
        }
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (drag && isCurrentlyActive.not()) {
            drag = false
            onDragListener.forEach {
                it(false)
            }
            if(removeFlag) {
                toRemove = viewHolder
                viewHolder.itemView.animate().alpha(0.0f).setDuration(FADE_ANIMATION_DURATION)
                    .start()
            }
        }

        val yOffset = max(0.0f, dY)

        if (dX == 0.0f || yOffset == 0.0f) {
            viewHolder.itemView.rotation = 0.0f
        } else {
            viewHolder.itemView.rotation =
                -atan(dX / recyclerView.height) * 180.0f / PI.toFloat()
        }
        with(viewHolder.itemView) {
            val scale = (recyclerView.width - yOffset) / recyclerView.width.toFloat()
            scaleX = max(MIN_CARD_SCALE, scale)
            scaleY = max(MIN_CARD_SCALE, scale)
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun isLongPressDragEnabled(): Boolean {
        drag = true
        onDragListener.forEach {
            it(drag)
        }
        return super.isLongPressDragEnabled()
    }

    override fun interpolateOutOfBoundsScroll(
        recyclerView: RecyclerView,
        viewSize: Int,
        viewSizeOutOfBounds: Int,
        totalSize: Int,
        msSinceStartScroll: Long
    ): Int {
        return 0
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        selected = viewHolder
        super.onSelectedChanged(viewHolder, actionState)
    }

    fun doWithSelectedHolder(action: (RecyclerView.ViewHolder?)->Unit) {
        action(selected)
    }
}