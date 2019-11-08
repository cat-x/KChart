package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Pair
import androidx.core.content.ContextCompat
import xyz.a1api.kchart.R
import xyz.a1api.kchart.entity.IMACD
import xyz.a1api.kchart.utils.CanvasUtils
import xyz.a1api.kchart.utils.XAlign
import xyz.a1api.kchart.utils.YAlign
import xyz.a1api.kchart.utils.dip2px
import xyz.a1api.kchart.view.BaseKChartView

/**
 * Created by tifezh on 2018/3/30.
 * For KChart
 * Cat-x All Rights Reserved
 */

class MACDDraw(rect: Rect, view: BaseKChartView) : BaseChartDraw<IMACD>(rect, view) {

    private val mRedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGreenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDIFPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDEAPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mMACDPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**macd 中柱子的宽度 */
    private var mMACDWidth = 10f

    init {
        val context = view.context
        mRedPaint.color = ContextCompat.getColor(context, R.color.chart_red)
        mGreenPaint.color = ContextCompat.getColor(context, R.color.chart_green)

        setLineWidth(mKChartView.getLineWidth())
        setTextSize(mKChartView.getTextSize())
        mDEAPaint.color = ContextCompat.getColor(context, R.color.chart_ma1)
        mDIFPaint.color = ContextCompat.getColor(context, R.color.chart_ma2)
    }

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()
//        val x = mKChartView.getTextPaint().measureText(getValueFormatter().format(getMaxValue()) + " ")
        CanvasUtils.drawTexts(
            canvas, mKChartView.context.dip2px(10f).toFloat(), 0f, XAlign.LEFT, YAlign.TOP,
            Pair(mMACDPaint, "  MACD${IMACD.IMACDConfig.useMACDConfig}  "),
            Pair(mMACDPaint, "MACD:" + mKChartView.formatValue(point.getMacd()) + " "),
            Pair(mDIFPaint, "DIF:" + mKChartView.formatValue(point.getDif()) + " "),
            Pair(mDEAPaint, "DEA:" + mKChartView.formatValue(point.getDea()) + " ")

        )
    }

    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IMACD,
        lastPoint: IMACD,
        startX: Float,
        stopX: Float
    ) {
        drawMACD(canvas, curIndex, curPoint.getMacd())
        drawLine(canvas, mDEAPaint, curIndex, curPoint.getDea(), lastPoint.getDea(), startX, stopX)
        drawLine(canvas, mDIFPaint, curIndex, curPoint.getDif(), lastPoint.getDif(), startX, stopX)
    }

    public override fun getMaxValue(point: IMACD): Float {
        return Math.max(point.getMacd(), Math.max(point.getDea(), point.getDif()))
    }

    public override fun getMinValue(point: IMACD): Float {
        return Math.min(point.getMacd(), Math.min(point.getDea(), point.getDif()))
    }

    /**
     * 画macd
     */
    private fun drawMACD(canvas: Canvas, index: Int, macd: Float) {
        if (macd > 0) {
            drawRect(canvas, mRedPaint, index, mMACDWidth, macd, 0f)
        } else {
            drawRect(canvas, mGreenPaint, index, mMACDWidth, 0f, macd)
        }
    }

    /**
     * 设置DIF颜色
     */
    fun setDIFColor(color: Int) {
        this.mDIFPaint.color = color
    }

    /**
     * 设置DEA颜色
     */
    fun setDEAColor(color: Int) {
        this.mDEAPaint.color = color
    }

    /**
     * 设置MACD颜色
     */
    fun setMACDColor(color: Int) {
        this.mMACDPaint.color = color
    }

    /**
     * 设置MACD的宽度
     * @param MACDWidth
     */
    fun setMACDWidth(MACDWidth: Float) {
        mMACDWidth = MACDWidth
    }

    /**
     * 设置曲线宽度
     */
    fun setLineWidth(width: Float) {
        mDEAPaint.strokeWidth = width
        mDIFPaint.strokeWidth = width
        mMACDPaint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setTextSize(textSize: Float) {
        mDEAPaint.textSize = textSize
        mDIFPaint.textSize = textSize
        mMACDPaint.textSize = textSize
    }
}
