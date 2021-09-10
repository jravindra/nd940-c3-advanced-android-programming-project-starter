package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var initialLeftPositionX = 0f
    private var initialLeftPositionY = 0f
    private var rightPositionX = 0f
    private var rightPositionY = 0f
    private var loading = false
    var nextAngle = 0f
    private var rectF = RectF(
        (widthSize * 3 / 4).toFloat(),
        (heightSize / 8).toFloat(),
        (widthSize * 3 / 4).toFloat() + 20f,
        (heightSize / 8).toFloat() + 20f
    )

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (old != new) {
            invalidate()
        }
    }

    fun reset() {
        initialLeftPositionX = 0f
        initialLeftPositionY = 0f
        rightPositionX = 0f
        rightPositionY = 0f
        loading = false
        isClickable = true
        nextAngle = 0f
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        invalidate()
    }

    private val initialText = context.getString(R.string.button_initial_text)
    private val loadingText = context.getString(R.string.button_loading_text)

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 30.0f
        color = ContextCompat.getColor(context, R.color.white)
        typeface = Typeface.create("", Typeface.BOLD)
    }
    private val infoPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 30.0f
        alpha = 0
        color = ContextCompat.getColor(context, R.color.white)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val overlayPaint = Paint().apply {
        style = Paint.Style.FILL
        isDither = true
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 30.0f
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val loadingPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 30.0f
        alpha = 0
        color = ContextCompat.getColor(context, R.color.colorAccent)
        typeface = Typeface.create("", Typeface.BOLD)
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            setBackgroundColor(getColor(R.styleable.LoadingButton_loading_button_background, 0))
            textPaint.color = getColor(R.styleable.LoadingButton_loading_button_text_color, 0)
        }
        isClickable = true
        loading = false

    }


    override fun performClick(): Boolean {
        super.performClick()
        contentDescription = resources.getString(R.string.button_name)
        loading = true
        startAnimation()
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!loading) {
            canvas.drawText(
                initialText,
                (widthSize / 2).toFloat(),
                (heightSize / 2).toFloat(),
                textPaint
            )
        }

        if (loading) {
            initialLeftPositionX
            initialLeftPositionY = heightSize.toFloat()
            rightPositionX += 10
            rightPositionY += 10 - heightSize
            canvas.drawRect(
                initialLeftPositionX,
                initialLeftPositionY,
                rightPositionX,
                rightPositionY,
                overlayPaint
            )

            canvas.drawText(
                loadingText,
                (widthSize / 2).toFloat(),
                (heightSize / 2).toFloat(),
                infoPaint
            )
            nextAngle += 10f
            canvas.drawArc(
                rectF,
                0f,
                nextAngle,
                true,
                loadingPaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun startAnimation() {
        rectF = RectF(
            (widthSize * 2 / 3).toFloat(),
            (heightSize / 4).toFloat(),
            (widthSize * 4 / 6).toFloat() + 80f,
            (heightSize / 4).toFloat() + 80f
        )
        valueAnimator.setIntValues(0, 1000)
        valueAnimator.duration = 1200
        valueAnimator.addUpdateListener {
            invalidate()
        }
        valueAnimator.start()
    }

}