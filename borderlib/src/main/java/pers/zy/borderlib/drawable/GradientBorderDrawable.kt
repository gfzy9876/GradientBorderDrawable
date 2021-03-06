package pers.zy.borderlib.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import pers.zy.borderlib.utils.dpF

/**
 * date: 2020/8/27   time: 4:07 PM
 * author zy
 * Have a nice day :)
 **/
class GradientBorderDrawable(private var borderColors: IntArray = DEFAULT_COLORS,
                             private var bgColors: IntArray = DEFAULT_COLORS,
                             private var borderWidth: Float = DEFAULT_BORDER_WIDTH,
                             private var corner: Float = DEFAULT_CORNER,
                             private val borderAngle: Int = ANGLE_LEFT_RIGHT,
                             private var bgAngle: Int = ANGLE_LEFT_RIGHT) : Drawable() {

    companion object {
        const val DEFAULT_COLOR = Color.TRANSPARENT
        @JvmField val DEFAULT_COLORS = intArrayOf(DEFAULT_COLOR, DEFAULT_COLOR)
        @JvmField val DEFAULT_BORDER_WIDTH = 5f.dpF
        @JvmField val DEFAULT_CORNER = 10f.dpF

        const val ANGLE_LEFT_RIGHT = 1
        const val ANGLE_TOP_BOTTOM = 2
        const val ANGLE_LEFT_TOP_BOTTOM_RIGHT = 3
        const val ANGLE_LEFT_BOTTOM_RIGHT_TOP = 4
    }

    constructor(borderColor: Int, bgColor: Int, borderWidth: Float, corner: Float, borderAngle: Int, bgAngle: Int)
            : this(intArrayOf(borderColor, borderColor), intArrayOf(bgColor, bgColor), borderWidth, corner, borderAngle, bgAngle)

    constructor(borderColor: Int, bgColor: Int, borderWidth: Float, corner: Float)
            : this(intArrayOf(borderColor, borderColor), intArrayOf(bgColor, bgColor), borderWidth, corner = corner)

    private val roundRectF = RectF()
    private val drawRectF = RectF()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
        style = Paint.Style.STROKE
    }

    init {
        setStrokeWidth(borderWidth)
        updateShader()
    }

    fun updateCorner(corner: Float) {
        this.corner = corner
        invalidateSelf()
    }

    fun updateBorderWidth(borderWidth: Float) {
        this.borderWidth = borderWidth
        setStrokeWidth(borderWidth)
        invalidateSelf()
    }

    private fun setStrokeWidth(borderWidth: Float) {
        borderPaint.strokeWidth = borderWidth
        bgPaint.strokeWidth = borderWidth
    }

    private fun updateShader() {
        bgPaint.shader = getGradient(bgColors, bgAngle)
        borderPaint.shader = getGradient(borderColors, borderAngle)
    }

    private fun getGradient(colors: IntArray, angle: Int): LinearGradient {
        var startX = 0f
        var startY = 0f
        var endX = roundRectF.width()
        var endY = 0f
        when (angle) {
            ANGLE_TOP_BOTTOM -> {
                endX = 0f
                endY = roundRectF.height()
            }
            ANGLE_LEFT_TOP_BOTTOM_RIGHT -> {
                endY = roundRectF.height()
            }
            ANGLE_LEFT_BOTTOM_RIGHT_TOP -> {
                startY = roundRectF.height()
            }
        }
        return LinearGradient(startX, startY, endX, endY, colors, null, Shader.TileMode.CLAMP)
    }

    override fun getIntrinsicWidth(): Int = roundRectF.width().toInt()

    override fun getIntrinsicHeight(): Int = roundRectF.height().toInt()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        roundRectF.set(bounds)
        updateShader()
    }

    override fun draw(canvas: Canvas) {
        val space = borderWidth / 2f
        drawRectF.set(space, space, roundRectF.width() - space, roundRectF.height() - space)
        canvas.drawRoundRect(drawRectF, corner, corner, borderPaint)
        drawRectF.set(borderWidth, borderWidth, roundRectF.width() - borderWidth, roundRectF.height() - borderWidth)
        canvas.drawRoundRect(drawRectF, corner - space, corner - space, bgPaint)
    }

    override fun setAlpha(alpha: Int) {
        bgPaint.alpha = alpha
        borderPaint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun setColorFilter(colorFilter: ColorFilter?) { }
}