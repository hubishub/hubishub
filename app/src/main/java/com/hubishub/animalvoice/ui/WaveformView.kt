package com.hubishub.animalvoice.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs
import kotlin.random.Random

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val barCount = 24
    private val bars = FloatArray(barCount) { 0f }
    private var currentAmplitude = 0

    fun updateAmplitude(amplitude: Int) {
        currentAmplitude = amplitude
        shiftBarsLeft()
        val normalized = (amplitude / 32767f).coerceIn(0f, 1f)
        val jitter = if (normalized > 0.02f) Random.nextFloat() * 0.15f else 0f
        bars[barCount - 1] = normalized + jitter
        invalidate()
    }

    private fun shiftBarsLeft() {
        for (i in 0 until barCount - 1) {
            bars[i] = bars[i + 1] * 0.92f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val totalWidth = width.toFloat()
        val totalHeight = height.toFloat()
        val barWidth = (totalWidth / barCount) * 0.6f
        val gap = (totalWidth / barCount) * 0.4f
        val minBarHeight = 6f
        val maxBarHeight = totalHeight * 0.85f
        val centerY = totalHeight / 2f

        for (i in 0 until barCount) {
            val barHeight = minBarHeight + bars[i] * (maxBarHeight - minBarHeight)
            val left = i * (barWidth + gap) + gap / 2
            val right = left + barWidth
            val top = centerY - barHeight / 2f
            val bottom = centerY + barHeight / 2f

            val alpha = 100 + (155 * (i.toFloat() / barCount)).toInt()
            paint.alpha = alpha

            canvas.drawRoundRect(
                RectF(left, top, right, bottom),
                barWidth / 2, barWidth / 2,
                paint
            )
        }
    }
}
