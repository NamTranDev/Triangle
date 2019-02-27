package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.Math.*
import kotlin.math.sqrt

class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /*
    * Assume cx = 9, cy = 9, r = 6, and a horizontal base.
    * First, find the length of the sides of the triangle (a,b,c):
    * 9r^2 = a^2 + b^2 + c^2
    * r^2 = 36, 9r^2 = 324, 324/3 = 108, sqrt(432) = 10.39
    * Once we know the length of each side of the triangle (s = 10.39),
    * we can calculate for x coordinates. Add s/2 (5.2) to cx for Bx (14.2), and subtract s/2 from cx for Ax (3.8).
    *
    * x solved now need y
    *
    * Speaking of s/2, if we split the triangle in half vertically (from point C to midpoint between points B & A)
    * , we can solve for y (ultimately giving us Ay and By):
    *
    * a^2 + b^2 = c^2
    * a^2 + 27.04 (1/2 s squared) = 107.95 (length s squared)
    * a^2 = 80.91
    * sqrt(80.91) = 8.99
    *
    * Subtract this y value from cy (9 + 8.99 = 17.99) gives us our new y plot for both points A and B.
    *
    * Center ( 9.00, 9.00)
    * C      ( 9.00,15.00)
    * B      (14.20, 17.99)
    * A      ( 3.80, 17.99)
    *
    */

    var paint: Paint
    var paintPoint: Paint
    var centerPoint: Point
    var peekPoint: PointF
    var leftPoint: PointF
    var rightPoint: PointF
    val path = Path()
    val radiusDistance = 200f

    init {
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))

        paint = Paint()
        paint.isAntiAlias = true
        paint.color = android.graphics.Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        paintPoint = Paint()
        paintPoint.isAntiAlias = true
        paintPoint.color = android.graphics.Color.WHITE
        paintPoint.style = Paint.Style.FILL
        paintPoint.strokeWidth = 10f

        centerPoint = Point()
        peekPoint = PointF()
        leftPoint = PointF()
        rightPoint = PointF()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerPoint.set(w / 2, h / 2)

        peekPoint.set(findPeekPointX(), findPeekPointY())
        leftPoint.set(findLeftPointX(), centerPoint.y + findDistancePointYLeftRight())
        rightPoint.set(findRightPointX(), centerPoint.y + findDistancePointYLeftRight())

        path.moveTo(peekPoint.x, peekPoint.y)
        path.lineTo(leftPoint.x, leftPoint.y)
        path.lineTo(rightPoint.x, rightPoint.y)
        path.lineTo(peekPoint.x, peekPoint.y)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return
        canvas.drawCircle(centerPoint.x.toFloat(), centerPoint.y.toFloat(), radiusDistance, paint)
        canvas.drawCircle(centerPoint.x.toFloat(), centerPoint.y.toFloat(), findDistancePointYLeftRight(), paint)

        canvas.drawPoint(centerPoint.x.toFloat(), centerPoint.y.toFloat(), paintPoint)
        canvas.drawPoint(peekPoint.x, peekPoint.y, paintPoint)
        canvas.drawPoint(leftPoint.x, leftPoint.y, paintPoint)
        canvas.drawPoint(rightPoint.x, rightPoint.y, paintPoint)
        canvas.drawPath(path, paint)
    }


    fun findEdgeLength(): Float {
        return 3 * radiusDistance / sqrt(3f)
    }

    fun findPeekPointX(): Float {
        return centerPoint.x.toFloat()
    }

    fun findPeekPointY(): Float {
        return centerPoint.y.toFloat() - radiusDistance
    }

    fun findLeftPointX(): Float {
        return centerPoint.x - findEdgeLength() / 2
    }

    fun findRightPointX(): Float {
        return centerPoint.x + findEdgeLength() / 2
    }

    fun findDistancePointYLeftRight(): Float {
        return Math.sqrt(pow(findEdgeLength().toDouble(), 2.0) - pow((findEdgeLength() / 2).toDouble(), 2.0)).toFloat() - radiusDistance
    }
}