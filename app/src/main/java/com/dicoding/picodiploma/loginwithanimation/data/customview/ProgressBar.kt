package com.dicoding.picodiploma.loginwithanimation.data.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat

class ProgressBar : ProgressBar {
    private lateinit var textPaint: Paint
    private lateinit var circlePaint: Paint
    private var currentProgress = 0
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setWillNotDraw(false)
        circlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        val whileColor = ContextCompat.getColor(context, android.R.color.white)
        circlePaint.color = whileColor

        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        val blackColor = ContextCompat.getColor(context, android.R.color.black)
        textPaint.color = blackColor
        textPaint.textSize = 20F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = w.coerceAtMost(h)
        setMeasuredDimension(size, size)
    }

    private val r: Rect = Rect()

    override fun onDraw(canvas: Canvas) {

        val circleX = (width / 2).toFloat()
        val circleY = (height / 2).toFloat()
        val radius = (width / 2.5).toFloat()
        canvas.drawCircle(circleX, circleY, radius, circlePaint)

        val textToPrint = "$currentProgress %"
        canvas.getClipBounds(r)
        val cHeight: Int = r.height()
        val cWidth: Int = r.width()
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.getTextBounds(textToPrint, 0, textToPrint.length, r)
        val x: Float = cWidth / 2f - r.width() / 2f - r.left
        val y: Float = cHeight / 2f + r.height() / 2f - r.bottom
        canvas.drawText(textToPrint, x, y, textPaint)
        super.onDraw(canvas)
    }

    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        if (progress == 100) {
            visibility = View.GONE
            return
        }
        currentProgress = progress
        invalidate()
    }
}