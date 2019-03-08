package com.example.myapplication

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import java.lang.Math.pow
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

    private var paint1st: Paint
    private var paint2nd: Paint
    private var paint3rd: Paint
    private var paint4th: Paint
    private var paintPoint: Paint
    private var centerBottomPoint: Point
    private var centerTopPoint: Point
    private var peekPoint: PointF
    private var leftPoint: PointF
    private var rightPoint: PointF
    private val pathTop1st = Path()
    private val pathTop2nd = Path()
    private val pathTop3rd = Path()
    private val pathTop4th = Path()
    private val pathBottom1st = Path()
    private val pathBottom2nd = Path()
    private val pathBottom3rd = Path()
    private val pathBottom4th = Path()
    private val radiusDistance = 200f
    private var valueAnimator1st: ValueAnimator
    private var valueAnimator2nd: ValueAnimator
    private var valueAnimator3rd: ValueAnimator
    private var valueAnimator4th: ValueAnimator
    private var mDurationMax = 4000L

    val listCenterTop = mutableListOf<Point>()
    val listCenterBottom = mutableListOf<Point>()

    init {
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))

        paint1st = Paint()
        paint1st.isAntiAlias = true
        paint1st.color = Color.parseColor("#ff66cc")
        paint1st.style = Paint.Style.STROKE
        paint1st.strokeWidth = 5f

        paint2nd = Paint()
        paint2nd.isAntiAlias = true
        paint2nd.color = Color.parseColor("#6666ff")
        paint2nd.style = Paint.Style.STROKE
        paint2nd.strokeWidth = 5f

        paint3rd = Paint()
        paint3rd.isAntiAlias = true
        paint3rd.color = Color.parseColor("#66ffcc")
        paint3rd.style = Paint.Style.STROKE
        paint3rd.strokeWidth = 5f

        paint4th = Paint()
        paint4th.isAntiAlias = true
        paint4th.color = Color.parseColor("#ccff66")
        paint4th.style = Paint.Style.STROKE
        paint4th.strokeWidth = 5f

        paintPoint = Paint()
        paintPoint.isAntiAlias = true
        paintPoint.color = android.graphics.Color.WHITE
        paintPoint.style = Paint.Style.FILL
        paintPoint.strokeWidth = 10f

        centerBottomPoint = Point()
        centerTopPoint = Point()
        peekPoint = PointF()
        leftPoint = PointF()
        rightPoint = PointF()

        valueAnimator1st = ValueAnimator.ofFloat(0f, radiusDistance)
        valueAnimator1st.interpolator = LinearInterpolator()
        valueAnimator1st.duration = mDurationMax
        valueAnimator1st.repeatCount = INFINITE
        valueAnimator1st.addUpdateListener {
            val value = it.animatedValue as Float
            Log.d("animatedValue", "" + value)
            peekPoint.set(findPeekPointX(centerBottomPoint), findPeekPointY(centerBottomPoint,value,false))
            leftPoint.set(findLeftPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))
            rightPoint.set(findRightPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))

            pathBottom1st.reset()
            pathBottom1st.moveTo(peekPoint.x, peekPoint.y)
            pathBottom1st.lineTo(leftPoint.x, leftPoint.y)
            pathBottom1st.lineTo(rightPoint.x, rightPoint.y)
            pathBottom1st.lineTo(peekPoint.x, peekPoint.y)

            peekPoint.set(findPeekPointX(centerTopPoint), findPeekPointY(centerTopPoint,radiusDistance - value,true))
            leftPoint.set(findLeftPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))
            rightPoint.set(findRightPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))

            pathTop1st.reset()
            pathTop1st.moveTo(peekPoint.x, peekPoint.y)
            pathTop1st.lineTo(leftPoint.x, leftPoint.y)
            pathTop1st.lineTo(rightPoint.x, rightPoint.y)
            pathTop1st.lineTo(peekPoint.x, peekPoint.y)
            invalidate()
        }

        valueAnimator2nd = ValueAnimator.ofFloat(0f, radiusDistance)
        valueAnimator1st.interpolator = LinearInterpolator()
        valueAnimator2nd.duration = mDurationMax
        valueAnimator2nd.startDelay = mDurationMax / 4
        valueAnimator2nd.repeatCount = INFINITE
        valueAnimator2nd.addUpdateListener {
            val value = it.animatedValue as Float
//            Log.d("animatedValue", "" + value)
            peekPoint.set(findPeekPointX(centerBottomPoint), findPeekPointY(centerBottomPoint,value,false))
            leftPoint.set(findLeftPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))
            rightPoint.set(findRightPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))

            pathBottom2nd.reset()
            pathBottom2nd.moveTo(peekPoint.x, peekPoint.y)
            pathBottom2nd.lineTo(leftPoint.x, leftPoint.y)
            pathBottom2nd.lineTo(rightPoint.x, rightPoint.y)
            pathBottom2nd.lineTo(peekPoint.x, peekPoint.y)

            peekPoint.set(findPeekPointX(centerTopPoint), findPeekPointY(centerTopPoint,radiusDistance - value,true))
            leftPoint.set(findLeftPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))
            rightPoint.set(findRightPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))

            pathTop2nd.reset()
            pathTop2nd.moveTo(peekPoint.x, peekPoint.y)
            pathTop2nd.lineTo(leftPoint.x, leftPoint.y)
            pathTop2nd.lineTo(rightPoint.x, rightPoint.y)
            pathTop2nd.lineTo(peekPoint.x, peekPoint.y)

            invalidate()
        }

        valueAnimator3rd = ValueAnimator.ofFloat(0f, radiusDistance)
        valueAnimator3rd.interpolator = LinearInterpolator()
        valueAnimator3rd.duration = mDurationMax
        valueAnimator3rd.startDelay = mDurationMax / 2
        valueAnimator3rd.repeatCount = INFINITE
        valueAnimator3rd.addUpdateListener {
            val value = it.animatedValue as Float
//            Log.d("animatedValue", "" + value)
            peekPoint.set(findPeekPointX(centerBottomPoint), findPeekPointY(centerBottomPoint,value,false))
            leftPoint.set(findLeftPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))
            rightPoint.set(findRightPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))

            pathBottom3rd.reset()
            pathBottom3rd.moveTo(peekPoint.x, peekPoint.y)
            pathBottom3rd.lineTo(leftPoint.x, leftPoint.y)
            pathBottom3rd.lineTo(rightPoint.x, rightPoint.y)
            pathBottom3rd.lineTo(peekPoint.x, peekPoint.y)

            peekPoint.set(findPeekPointX(centerTopPoint), findPeekPointY(centerTopPoint,radiusDistance - value,true))
            leftPoint.set(findLeftPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))
            rightPoint.set(findRightPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))

            pathTop3rd.reset()
            pathTop3rd.moveTo(peekPoint.x, peekPoint.y)
            pathTop3rd.lineTo(leftPoint.x, leftPoint.y)
            pathTop3rd.lineTo(rightPoint.x, rightPoint.y)
            pathTop3rd.lineTo(peekPoint.x, peekPoint.y)

            invalidate()
        }

        valueAnimator4th = ValueAnimator.ofFloat(0f, radiusDistance)
        valueAnimator4th.interpolator = LinearInterpolator()
        valueAnimator4th.duration = mDurationMax
        valueAnimator4th.startDelay = mDurationMax * 3 / 4
        valueAnimator4th.repeatCount = INFINITE
        valueAnimator4th.addUpdateListener {
            val value = it.animatedValue as Float
//            Log.d("animatedValue", "" + value)
            peekPoint.set(findPeekPointX(centerBottomPoint), findPeekPointY(centerBottomPoint,value,false))
            leftPoint.set(findLeftPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))
            rightPoint.set(findRightPointX(centerBottomPoint,value), findLeftRightPointY(centerBottomPoint,value,false))

            pathBottom4th.reset()
            pathBottom4th.moveTo(peekPoint.x, peekPoint.y)
            pathBottom4th.lineTo(leftPoint.x, leftPoint.y)
            pathBottom4th.lineTo(rightPoint.x, rightPoint.y)
            pathBottom4th.lineTo(peekPoint.x, peekPoint.y)

            peekPoint.set(findPeekPointX(centerTopPoint), findPeekPointY(centerTopPoint,radiusDistance - value,true))
            leftPoint.set(findLeftPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))
            rightPoint.set(findRightPointX(centerTopPoint, radiusDistance - value), findLeftRightPointY(centerTopPoint,radiusDistance - value,true))

            pathTop4th.reset()
            pathTop4th.moveTo(peekPoint.x, peekPoint.y)
            pathTop4th.lineTo(leftPoint.x, leftPoint.y)
            pathTop4th.lineTo(rightPoint.x, rightPoint.y)
            pathTop4th.lineTo(peekPoint.x, peekPoint.y)

            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerBottomPoint.set(w/2, h/2)

        centerTopPoint.set(centerBottomPoint.x,centerBottomPoint.y - radiusDistance.toInt())

//        val isFalse = true
//        while (isFalse){
//
//        }

        valueAnimator1st.start()
        valueAnimator2nd.start()
        valueAnimator3rd.start()
        valueAnimator4th.start()


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return
//        canvas.drawCircle(centerBottomPoint.x.toFloat(), centerBottomPoint.y.toFloat(), radiusDistance, paint1st)
//        canvas.drawCircle(centerBottomPoint.x.toFloat(), centerBottomPoint.y.toFloat(), findDistancePointYLeftRightToCenter(radiusDistance), paint1st)

//        canvas.drawPoint(centerBottomPoint.x.toFloat(), centerBottomPoint.y.toFloat(), paintPoint)
        canvas.drawPath(pathBottom1st, paint1st)
        canvas.drawPath(pathBottom2nd, paint2nd)
        canvas.drawPath(pathBottom3rd, paint3rd)
        canvas.drawPath(pathBottom4th, paint4th)
        canvas.drawPath(pathTop1st, paint1st)
        canvas.drawPath(pathTop2nd, paint2nd)
        canvas.drawPath(pathTop3rd, paint3rd)
        canvas.drawPath(pathTop4th, paint4th)
    }


    fun findEdgeLength(radius: Float): Float {
        return 3 * radius / sqrt(3f)
    }

    fun findPeekPointX(center : Point): Float {
        return center.x.toFloat()
    }

    fun findPeekPointY(center : Point,radius: Float,isPeekTop : Boolean): Float {
        return if (isPeekTop) center.y.toFloat() - radius else center.y.toFloat() + radius
    }

    fun findLeftPointX(center : Point,radius: Float): Float {
        return center.x - findEdgeLength(radius) / 2
    }

    fun findRightPointX(center : Point,radius: Float): Float {
        return center.x + findEdgeLength(radius) / 2
    }

    fun findDistancePointYLeftRightToCenter(radius: Float): Float {
        return Math.sqrt(
            pow(findEdgeLength(radius).toDouble(), 2.0) - pow(
                (findEdgeLength(radius) / 2).toDouble(),
                2.0
            )
        ).toFloat() - radius
    }

    fun findLeftRightPointY(center : Point,radius: Float,isPeekTop : Boolean): Float {
        return if (isPeekTop) center.y + findDistancePointYLeftRightToCenter(radius)
        else findPeekPointY(center,radius,isPeekTop) - radius - findDistancePointYLeftRightToCenter(radius)
    }
}