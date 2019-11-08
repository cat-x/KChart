package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Pair
import xyz.a1api.kchart.entity.IBOLL
import xyz.a1api.kchart.utils.CanvasUtils
import xyz.a1api.kchart.utils.XAlign
import xyz.a1api.kchart.utils.YAlign
import xyz.a1api.kchart.view.BaseKChartView

/**
 * BOLL实现类
 * Created by tifezh on 2016/6/19.
 * For KChart
 * Cat-x All Rights Reserved
 */

class BOLLDraw
/**
 * 构造方法
 *
 * @param rect       显示区域
 * @param KChartView [BaseKChartView]
 */
    (rect: Rect, KChartView: BaseKChartView) : BaseChartDraw<IBOLL>(rect, KChartView) {

    private val mUpPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mMbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDnPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()
        val x =
            mKChartView.getTextPaint().measureText(getValueFormatter().format(getMaxValue()) + " ")
        CanvasUtils.drawTexts(
            canvas, x, 0f, XAlign.LEFT, YAlign.TOP,
            Pair(mUpPaint, "UP:" + mKChartView.formatValue(point.getUp()) + " "),
            Pair(mMbPaint, "MB:" + mKChartView.formatValue(point.getMb()) + " "),
            Pair(mDnPaint, "DN:" + mKChartView.formatValue(point.getDn()) + " ")
        )
    }

    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IBOLL,
        lastPoint: IBOLL,
        startX: Float,
        stopX: Float
    ) {
        drawLine(canvas, mUpPaint, curIndex, curPoint.getUp(), lastPoint.getUp(), startX, stopX)
        drawLine(canvas, mMbPaint, curIndex, curPoint.getMb(), lastPoint.getMb(), startX, stopX)
        drawLine(canvas, mDnPaint, curIndex, curPoint.getDn(), lastPoint.getDn(), startX, stopX)
    }

    public override fun getMaxValue(point: IBOLL): Float {
        return if (java.lang.Float.isNaN(point.getUp())) {
            point.getMb()
        } else point.getUp()
    }

    public override fun getMinValue(point: IBOLL): Float {
        return if (java.lang.Float.isNaN(point.getDn())) {
            point.getMb()
        } else point.getDn()
    }

    /**
     * 设置up颜色
     */
    fun setUpColor(color: Int) {
        mUpPaint.color = color
    }

    /**
     * 设置mb颜色
     */
    fun setMbColor(color: Int) {
        mMbPaint.color = color
    }

    /**
     * 设置dn颜色
     */
    fun setDnColor(color: Int) {
        mDnPaint.color = color
    }

    /**
     * 设置曲线宽度
     */
    fun setLineWidth(width: Float) {
        mUpPaint.strokeWidth = width
        mMbPaint.strokeWidth = width
        mDnPaint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setTextSize(textSize: Float) {
        mUpPaint.textSize = textSize
        mMbPaint.textSize = textSize
        mDnPaint.textSize = textSize
    }


}
