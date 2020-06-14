package com.example.testproject

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class MyMarginItemDecoration(private val verticalBias: Float) : RecyclerView.ItemDecoration() {

    var parentWidth = -1//костыль
    var parentHeight = -1
    var viewWidth = -1
    var viewHeight= -1

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if(parentWidth == -1){
                parentWidth = parent.width
            }
            if(parentHeight == -1){
                parentHeight = parent.height
            }
            if(viewWidth == -1){
                viewWidth = view.width
            }
            if(viewHeight == -1){
                viewHeight = view.height
            }
            top = (verticalBias * (parentHeight - viewHeight)).roundToInt()
            bottom = parentHeight - top
            left = (parentWidth - viewWidth) / 2
            right = left
        }
    }
}