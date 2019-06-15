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
    private val pathTop1st = mutableListOf<Path>()
    private val pathTop2nd = mutableListOf<Path>()
    private val pathTop3rd = mutableListOf<Path>()
    private val pathTop4th = mutableListOf<Path>()
    private val pathBottom1st = mutableListOf<Path>()
    private val pathBottom2nd = mutableListOf<Path>()
    private val pathBottom3rd = mutableListOf<Path>()
    private val pathBottom4th = mutableListOf<Path>()
    private val radiusDistance = 100f
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
        paint1st.strokeWidth = 2f

        paint2nd = Paint()
        paint2nd.isAntiAlias = true
        paint2nd.color = Color.parseColor("#6666ff")
        paint2nd.style = Paint.Style.STROKE
        paint2nd.strokeWidth = 2f

        paint3rd = Paint()
        paint3rd.isAntiAlias = true
        paint3rd.color = Color.parseColor("#66ffcc")
        paint3rd.style = Paint.Style.STROKE
        paint3rd.strokeWidth = 2f

        paint4th = Paint()
        paint4th.isAntiAlias = true
        paint4th.color = Color.parseColor("#ccff66")
        paint4th.style = Paint.Style.STROKE
        paint4th.strokeWidth = 2f

        paintPoint = Paint()
        paintPoint.isAntiAlias = true
        paintPoint.color = android.graphics.Color.WHITE
        paintPoint.style = Paint.Style.FILL
        paintPoint.strokeWidth = 5f

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

            pathBottom1st.clear()
            pathTop1st.clear()
            for (point in listCenterBottom) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point, value, false))
                leftPoint.set(findLeftPointX(point, value), findLeftRightPointY(point, value, false))
                rightPoint.set(findRightPointX(point, value), findLeftRightPointY(point, value, false))

                val path = Path()
                path.reset()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathBottom1st.add(path)
            }

            for (point in listCenterTop) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point, radiusDistance - value, true))
                leftPoint.set(
                    findLeftPointX(point, radiusDistance - value),
                    findLeftRightPointY(point, radiusDistance - value, true)
                )
                rightPoint.set(
                    findRightPointX(point, radiusDistance - value),
                    findLeftRightPointY(point, radiusDistance - value, true)
                )

                val path = Path()
                path.reset()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathTop1st.add(path)
            }

            Log.d("animatedValue", "" + value)




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

            pathBottom2nd.clear()
            pathTop2nd.clear()
            for (point in listCenterBottom) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point, value, false))
                leftPoint.set(findLeftPointX(point, value), findLeftRightPointY(point, value, false))
                rightPoint.set(findRightPointX(point, value), findLeftRightPointY(point, value, false))

                val path = Path()
                path.reset()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathBottom2nd.add(path)
            }

            for (point in listCenterTop) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point, radiusDistance - value, true))
                leftPoint.set(
                    findLeftPointX(point, radiusDistance - value),
                    findLeftRightPointY(point, radiusDistance - value, true)
                )
                rightPoint.set(
                    findRightPointX(point, radiusDistance - value),
                    findLeftRightPointY(point, radiusDistance - value, true)
                )

                val path = Path()
                path.reset()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathTop2nd.add(path)
            }

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

            pathBottom3rd.clear()
            pathTop3rd.clear()
            for (point in listCenterBottom) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point, value, false))
                leftPoint.set(findLeftPointX(point, value), findLeftRightPointY(point, value, false))
                rightPoint.set(findRightPointX(point, value), findLeftRightPointY(point, value, false))

                val path = Path()
                path.reset()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathBottom3rd.add(path)
            }

            for (point in listCenterTop) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point, radiusDistance - value, true))
                leftPoint.set(
                    findLeftPointX(point, radiusDistance - value),
                    findLeftRightPointY(point, radiusDistance - value, true)
                )
                rightPoint.set(
                    findRightPointX(point, radiusDistance - value),
                    findLeftRightPointY(point, radiusDistance - value, true)
                )

                val path = Path()
                path.reset()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathTop3rd.add(path)
            }

            invalidate()
        }

        valueAnimator4th = ValueAnimator.ofFloat(0f, radiusDistance)
        valueAnimator4th.interpolator = LinearInterpolator()
        valueAnimator4th.duration = mDurationMax
        val delay = mDurationMax * 3/4
        valueAnimator4th.startDelay = delay
        valueAnimator4th.repeatCount = INFINITE
        valueAnimator4th.addUpdateListener {
            val value = it.animatedValue as Float
//            Log.d("animatedValue", "" + value)

            pathBottom4th.clear()
            pathTop4th.clear()
            for (point in listCenterBottom) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point,value,false))
                leftPoint.set(findLeftPointX(point,value), findLeftRightPointY(point,value,false))
                rightPoint.set(findRightPointX(point,value), findLeftRightPointY(point,value,false))

                val path = Path()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathBottom4th.add(path)
            }

            for (point in listCenterTop) {
                peekPoint.set(findPeekPointX(point), findPeekPointY(point,radiusDistance - value,true))
                leftPoint.set(findLeftPointX(point, radiusDistance - value), findLeftRightPointY(point,radiusDistance - value,true))
                rightPoint.set(findRightPointX(point, radiusDistance - value), findLeftRightPointY(point,radiusDistance - value,true))

                val path = Path()
                path.moveTo(peekPoint.x, peekPoint.y)
                path.lineTo(leftPoint.x, leftPoint.y)
                path.lineTo(rightPoint.x, rightPoint.y)
                path.lineTo(peekPoint.x, peekPoint.y)
                pathTop4th.add(path)
            }

            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var isAllow = true
        var numberHorizon = 0
        var pointX = 0
        var pointY = 0

        while (isAllow) {
            if (pointX > (w + radiusDistance) && pointY > (h + radiusDistance)) {
                isAllow = false
            } else {
                if (pointX < (w + radiusDistance)) {
                    if (numberHorizon % 2 == 0) {
                        val point = Point(pointX, pointY)
                        listCenterTop.add(point)
                        listCenterBottom.add(Point(point.x, point.y + radiusDistance.toInt()))
                    } else {
                        val point = Point(pointX, pointY - radiusDistance.toInt() / 2)
                        listCenterBottom.add(point)
                        listCenterTop.add(Point(pointX,pointY + 2 * radiusDistance.toInt() - radiusDistance.toInt() / 2))
                    }
                    numberHorizon++
                    pointX += radiusDistance.toInt() - 12
                } else {
                    numberHorizon = 0
                    pointX = 0
                    pointY += 3 * radiusDistance.toInt()
                }
            }
        }

        valueAnimator1st.start()
        valueAnimator2nd.start()
        valueAnimator3rd.start()
        valueAnimator4th.start()


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

//        testCircle(canvas)

        for (path in pathBottom1st) {
            canvas.drawPath(path, paint1st)
        }
        for (path in pathBottom2nd) {
            canvas.drawPath(path, paint2nd)
        }
        for (path in pathBottom3rd) {
            canvas.drawPath(path, paint3rd)
        }
        for (path in pathBottom4th) {
            canvas.drawPath(path, paint4th)
        }
        for (path in pathTop1st) {
            canvas.drawPath(path, paint1st)
        }
        for (path in pathTop2nd) {
            canvas.drawPath(path, paint2nd)
        }
        for (path in pathTop3rd) {
            canvas.drawPath(path, paint3rd)
        }
        for (path in pathTop4th) {
            canvas.drawPath(path, paint4th)
        }
    }

    fun testCircle(canvas: Canvas) {
        canvas.drawCircle(centerBottomPoint.x.toFloat(), centerBottomPoint.y.toFloat(), radiusDistance, paint1st)
        canvas.drawCircle(
            centerBottomPoint.x.toFloat(),
            centerBottomPoint.y.toFloat(),
            findDistancePointYLeftRightToCenter(radiusDistance),
            paint1st
        )

        canvas.drawPoint(centerBottomPoint.x.toFloat(), centerBottomPoint.y.toFloat(), paintPoint)
        canvas.drawCircle(centerTopPoint.x.toFloat(), centerTopPoint.y.toFloat(), radiusDistance, paint1st)
        canvas.drawCircle(
            centerTopPoint.x.toFloat(),
            centerTopPoint.y.toFloat(),
            findDistancePointYLeftRightToCenter(radiusDistance),
            paint1st
        )

        canvas.drawPoint(centerTopPoint.x.toFloat(), centerTopPoint.y.toFloat(), paintPoint)
        for (point in listCenterTop) {
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), radiusDistance, paint1st)
            canvas.drawCircle(
                point.x.toFloat(),
                point.y.toFloat(),
                findDistancePointYLeftRightToCenter(radiusDistance),
                paint1st
            )

            canvas.drawPoint(point.x.toFloat(), point.y.toFloat(), paintPoint)
        }
        for (point in listCenterBottom) {
            canvas.drawCircle(
                point.x.toFloat(),
                point.y.toFloat(),
                radiusDistance,
                paint1st
            )
            canvas.drawCircle(
                point.x.toFloat(),
                point.y.toFloat(),
                findDistancePointYLeftRightToCenter(radiusDistance),
                paint1st
            )

            canvas.drawPoint(point.x.toFloat(), point.y.toFloat(), paintPoint)
        }
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