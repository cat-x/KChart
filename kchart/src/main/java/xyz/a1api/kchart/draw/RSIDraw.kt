package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Pair
import xyz.a1api.kchart.entity.IRSI
import xyz.a1api.kchart.utils.*
import xyz.a1api.kchart.view.BaseKChartView

/**
 * RSI实现类
 * Created by tifezh on 2016/6/19.
 * For KChart
 * Cat-x All Rights Reserved
 */

class RSIDraw
/**
 * 构造方法
 *
 * @param rect       显示区域
 * @param KChartView [BaseKChartView]
 */
    (rect: Rect, KChartView: BaseKChartView) : BaseChartDraw<IRSI>(rect, KChartView) {

    private val mRSI1Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRSI2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRSI3Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IRSI,
        lastPoint: IRSI,
        startX: Float,
        stopX: Float
    ) {
        IRSI.IRSIConfig.customizRSILists.unfold() { index, num, enable ->
            if (enable) {
                drawLine(
                    canvas,
                    getPaint(index),
                    curIndex,
                    curPoint.getRsiByNum(num),
                    lastPoint.getRsiByNum(num),
                    startX,
                    stopX
                )
            }
        }
    }

    private fun getPaint(index: Int): Paint {
        return when (index % 3) {
            0 -> mRSI1Paint
            1 -> mRSI2Paint
            2 -> mRSI3Paint
            else -> mRSI1Paint
        }
    }

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()
//        val x = mKChartView.getTextPaint().measureText(getValueFormatter().format(getMaxValue()) + " ")
        CanvasUtils.drawTexts(
            canvas, mKChartView.context.dip2px(10f).toFloat(), 0f, XAlign.LEFT, YAlign.TOP,
            *IRSI.IRSIConfig.customizRSILists.filter { it.value.enable }.map {
                Pair(
                    getPaint(it.key),
                    "RSI(${it.value.name}):" + mKChartView.formatValue(point.getRsiByNum(it.value.name)) + " "
                )
            }.toTypedArray()
        )
    }

    public override fun getMaxValue(point: IRSI): Float {
        return IRSI.IRSIConfig.getCalculateRSIConfig().map { point.getRsiByNum(it) }.max() ?: 0f
    }

    public override fun getMinValue(point: IRSI): Float {
        return IRSI.IRSIConfig.getCalculateRSIConfig().map { point.getRsiByNum(it) }.min() ?: 0f
    }

    fun setRSI1Color(color: Int) {
        mRSI1Paint.color = color
    }

    fun setRSI2Color(color: Int) {
        mRSI2Paint.color = color
    }

    fun setRSI3Color(color: Int) {
        mRSI3Paint.color = color
    }

    /**
     * 设置曲线宽度
     */
    fun setLineWidth(width: Float) {
        mRSI1Paint.strokeWidth = width
        mRSI2Paint.strokeWidth = width
        mRSI3Paint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setTextSize(textSize: Float) {
        mRSI2Paint.textSize = textSize
        mRSI3Paint.textSize = textSize
        mRSI1Paint.textSize = textSize
    }
}
