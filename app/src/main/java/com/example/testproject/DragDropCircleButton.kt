package com.example.testproject

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.math.MathUtils
import kotlin.math.max


//Лень в ресурсы добавлять
private const val DEFAULT_BASE_COLOR = Color.GRAY
private const val DEFAULT_OUTLINE_COLOR = Color.BLACK
private const val DEFAULT_STROKE_WIDTH = 5 //dp
private const val DEFAULT_SPEED = 2.5f
private const val DEFAULT_SCALE_MODIFIER = 1.0f

//Сделанно на отъебись, в реальных проектах офк такое не приемлимо
class DragDropCircleButton(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    var onCompleteListeners = mutableListOf<()->Unit>()

    private var angle = 0.0f
    private var alphaValue = 0.0f
        private set(value) {
            field = value
            outlinePaint.alpha = (value * 255).toInt()
        }
    private var angleSign = 0.0f

    var speed = DEFAULT_SPEED

    var scaleModifier = DEFAULT_SCALE_MODIFIER

    var baseColor = DEFAULT_BASE_COLOR
        set(value){
            field = value
            basePaint.color = value
        }
    var outlineColor = DEFAULT_OUTLINE_COLOR
        set(value){
            field = value
            outlinePaint.color = value
        }

    var strokeSize = DEFAULT_STROKE_WIDTH * resources.displayMetrics.density
        set(value){
            field = value
            basePaint.strokeWidth = value
            outlinePaint.strokeWidth = value
            updateRect()
        }

    private val rect by lazy {
        RectF(strokeSize,
        strokeSize,
        width - strokeSize,
        height - strokeSize)
    }

    val updateAnimation = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
        duration = Long.MAX_VALUE
        addUpdateListener {
            alphaValue += angleSign / 15.0f
            alphaValue = MathUtils.clamp(alphaValue, 0.0f, 1.0f)
            angle += max(angleSign, 0.0f) * speed
            val clamped = MathUtils.clamp(angle, 0.0f, 360.0f)
            if(alphaValue == 0.0f){
                angle = 0.0f
                cancel()
            }
            else if(angle != clamped){
                angle = clamped
                cancel()
            }
            invalidate()
        }
    }

    init {
        setWillNotDraw(false)
    }

    private fun updateRect(){
        rect.left = strokeSize
        rect.top = strokeSize
        rect.right = width - strokeSize
        rect.bottom = height - strokeSize
    }

    private fun isViewContains(view: View, rx: Int, ry: Int): Boolean {
        val l = IntArray(2)
        view.getLocationOnScreen(l)
        val x = l[0]
        val y = l[1]
        val w = view.width
        val h = view.height
        return !(rx < x || rx > x + w || ry < y || ry > y + h)
    }

    fun receiveTouch(event: MotionEvent){
        if(isViewContains(this, event.x.toInt(), event.y.toInt())
            && event.action != MotionEvent.ACTION_UP && event.action != MotionEvent.ACTION_CANCEL){
            angleSign = 1.0f
            updateAnimation.start()
            scaleX = scaleModifier
            scaleY = scaleModifier
        }
        else {
            if(event.action == MotionEvent.ACTION_UP){
                if(angle == 360.0f){
                    onCompleteListeners.forEach {
                        it()
                    }
                    angle = 0.0f
                }
            }
            angleSign = -1.0f
            updateAnimation.start()
            scaleX = scaleModifier
            scaleY = scaleModifier
        }
    }

    private val basePaint by lazy{
        Paint().apply {
            color = baseColor
            style = Paint.Style.STROKE
            strokeWidth = strokeSize
            isAntiAlias = true
        }
    }

    private val outlinePaint by lazy{
        Paint().apply {
            color = outlineColor
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
            strokeWidth = strokeSize
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f, basePaint)
        canvas?.drawArc(rect, -90.0f,  angle, false, outlinePaint)
        super.onDraw(canvas)
    }
}