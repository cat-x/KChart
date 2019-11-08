package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Pair
import xyz.a1api.kchart.base.IValueFormatter
import xyz.a1api.kchart.entity.IWR
import xyz.a1api.kchart.formatter.ValueFormatter
import xyz.a1api.kchart.utils.*
import xyz.a1api.kchart.view.BaseKChartView


/**
 * KDJ实现类
 * Created by tifezh on 2016/6/19.
 * For KChart
 * Cat-x All Rights Reserved
 */
class WRDraw(rect: Rect, kChartView: BaseKChartView) : BaseChartDraw<IWR>(rect, kChartView) {

    private val mR1Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mR2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mR3Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IWR,
        lastPoint: IWR,
        startX: Float,
        stopX: Float
    ) {

        IWR.IWRConfig.customizWRLists.unfold() { index, num, enable ->
            if (enable) {
                if (lastPoint.getRByNum(num) != -10f) {
                    drawLine(
                        canvas,
                        getPaint(index),
                        curIndex,
                        curPoint.getRByNum(num),
                        lastPoint.getRByNum(num),
                        startX,
                        stopX
                    )
                }
            }
        }

    }

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()
        CanvasUtils.drawTexts(
            canvas, mKChartView.context.dip2px(10f).toFloat(), 0f, XAlign.LEFT, YAlign.TOP,
            *IWR.IWRConfig.customizWRLists.filter { it.value.enable }.map {
                Pair(
                    getPaint(it.key),
                    "WR(${it.value.name}):${point.getRByNum(it.value.name)}  "
                )
            }.toTypedArray()
        )

    }


    override fun getMaxValue(point: IWR): Float {
        return IWR.IWRConfig.getCalculateWRConfig().map { point.getRByNum(it) }.max() ?: 0f
    }

    override fun getMinValue(point: IWR): Float {
        return IWR.IWRConfig.getCalculateWRConfig().map { point.getRByNum(it) }.min() ?: 0f
    }

    override fun getValueFormatter(): IValueFormatter {
        return ValueFormatter()
    }

    private fun getPaint(index: Int): Paint {
        return when (index % 3) {
            0 -> mR1Paint
            1 -> mR2Paint
            2 -> mR3Paint
            else -> mR1Paint
        }
    }

    /**
     * 设置%R颜色
     */
    fun setR1Color(color: Int) {
        mR1Paint.color = color
    }

    fun setR2Color(color: Int) {
        mR2Paint.color = color
    }

    fun setR3Color(color: Int) {
        mR3Paint.color = color
    }

    /**
     * 设置曲线宽度
     */
    fun setLineWidth(width: Float) {
        mR1Paint.strokeWidth = width
        mR2Paint.strokeWidth = width
        mR3Paint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setTextSize(textSize: Float) {
        mR1Paint.textSize = textSize
        mR2Paint.textSize = textSize
        mR3Paint.textSize = textSize
    }
}
