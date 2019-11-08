package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Pair
import xyz.a1api.kchart.entity.IKDJ
import xyz.a1api.kchart.utils.CanvasUtils
import xyz.a1api.kchart.utils.XAlign
import xyz.a1api.kchart.utils.YAlign
import xyz.a1api.kchart.utils.dip2px
import xyz.a1api.kchart.view.BaseKChartView

/**
 * KDJ实现类
 * Created by tifezh on 2016/6/19.
 * For KChart
 * Cat-x All Rights Reserved
 */

class KDJDraw(rect: Rect, KChartView: BaseKChartView) : BaseChartDraw<IKDJ>(rect, KChartView) {

    private val mKPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mJPaint = Paint(Paint.ANTI_ALIAS_FLAG)


    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IKDJ,
        lastPoint: IKDJ,
        startX: Float,
        stopX: Float
    ) {
        drawLine(canvas, mKPaint, curIndex, curPoint.getK(), lastPoint.getK(), startX, stopX)//画K线
        drawLine(canvas, mDPaint, curIndex, curPoint.getD(), lastPoint.getD(), startX, stopX)//画D线
        drawLine(canvas, mJPaint, curIndex, curPoint.getJ(), lastPoint.getJ(), startX, stopX)//画J线
    }

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()
        CanvasUtils.drawTexts(
            canvas, mKChartView.context.dip2px(10f).toFloat(), 0f, XAlign.LEFT, YAlign.TOP,
            Pair(mKPaint, "  KDJ${IKDJ.IKDJConfig.useKDJConfig}  "),
            Pair(mKPaint, "K:${mKChartView.formatValue(point.getK())}  "),
            Pair(mDPaint, "D:${mKChartView.formatValue(point.getD())}  "),
            Pair(mJPaint, "J:${mKChartView.formatValue(point.getJ())}  ")
        )
    }

    public override fun getMaxValue(point: IKDJ): Float {
        return maxOf(point.getK(), point.getD(), point.getJ())
    }

    public override fun getMinValue(point: IKDJ): Float {
        return minOf(point.getK(), point.getD(), point.getJ())
    }

    /**
     * 设置K颜色
     */
    fun setKColor(color: Int) {
        mKPaint.color = color
    }

    /**
     * 设置D颜色
     */
    fun setDColor(color: Int) {
        mDPaint.color = color
    }

    /**
     * 设置J颜色
     */
    fun setJColor(color: Int) {
        mJPaint.color = color
    }

    /**
     * 设置曲线宽度
     */
    fun setLineWidth(width: Float) {
        mKPaint.strokeWidth = width
        mDPaint.strokeWidth = width
        mJPaint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setTextSize(textSize: Float) {
        mKPaint.textSize = textSize
        mDPaint.textSize = textSize
        mJPaint.textSize = textSize
    }


}
