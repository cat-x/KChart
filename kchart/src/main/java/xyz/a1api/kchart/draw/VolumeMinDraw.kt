package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Pair
import androidx.core.content.ContextCompat
import xyz.a1api.kchart.R
import xyz.a1api.kchart.base.IValueFormatter
import xyz.a1api.kchart.entity.IVolume
import xyz.a1api.kchart.formatter.BigValueFormatter
import xyz.a1api.kchart.utils.CanvasUtils
import xyz.a1api.kchart.utils.XAlign
import xyz.a1api.kchart.utils.YAlign
import xyz.a1api.kchart.utils.dip2px
import xyz.a1api.kchart.view.BaseKChartView

/**
 * 成交量
 * Created by hjm on 2017/11/14 17:49.
 * For KChart
 * Cat-x All Rights Reserved
 */

class VolumeMinDraw
/**
 * 构造方法
 *
 * @param rect       显示区域
 * @param KChartView [BaseKChartView]
 */
    (rect: Rect, KChartView: BaseKChartView) : BaseChartDraw<IVolume>(rect, KChartView) {

    private val mVolumePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma5Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma10Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pillarWidth = 0

    init {
        val context = mKChartView.context
        mVolumePaint.color = ContextCompat.getColor(context, R.color.black_volume_min)
        pillarWidth = context.dip2px(3f)
    }


    private fun drawHistogram(
        canvas: Canvas, curPoint: IVolume, curX: Float
    ) {
        val r = (pillarWidth / 2).toFloat()
        val top = getY(curPoint.getVolume())
        val bottom = getY(0f)
        canvas.drawRect(curX - r, top, curX + r, bottom, mVolumePaint)
    }

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()
//        val x = mKChartView.getTextPaint().measureText(getValueFormatter().format(getMaxValue()) + " ")
        CanvasUtils.drawTexts(
            canvas, mKChartView.context.dip2px(10f).toFloat(), 0f, XAlign.LEFT, YAlign.TOP,
            Pair(
                mKChartView.getTextPaint(),
                "VOL:" + getValueFormatter().format(point.getVolume()) + " "
            )/*,*/
//            Pair(ma5Paint, "MA5:" + getValueFormatter().format(point.getMAVolumeByNum(5)) + " "),
//            Pair(ma10Paint, "MA10:" + getValueFormatter().format(point.getMAVolumeByNum(10)) + " ")
        )
    }

    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IVolume,
        lastPoint: IVolume,
        startX: Float,
        stopX: Float
    ) {
        drawHistogram(canvas, curPoint, getX(curIndex))
//        drawLine(canvas, ma5Paint, curIndex, curPoint.getMAVolumeByNum(5), lastPoint.getMAVolumeByNum(5))
//        drawLine(canvas, ma10Paint, curIndex, curPoint.getMAVolumeByNum(10), lastPoint.getMAVolumeByNum(10))
    }

    public override fun getMaxValue(point: IVolume): Float {
        return point.getVolume()
//        return Math.max(point.getVolume(), Math.max(point.getMAVolumeByNum(5), point.getMAVolumeByNum(10)))
    }

    public override fun getMinValue(point: IVolume): Float {
        return 0f
    }

    override fun getValueFormatter(): IValueFormatter {
        return BigValueFormatter()
    }

    /**
     * 设置 MA5 线的颜色
     *
     */
    fun setMa5Color(color: Int) {
        this.ma5Paint.color = color
    }

    /**
     * 设置 MA10 线的颜色
     */
    fun setMa10Color(color: Int) {
        this.ma10Paint.color = color
    }

    fun setLineWidth(width: Float) {
        this.ma5Paint.strokeWidth = width
        this.ma10Paint.strokeWidth = width
    }

    /**
     * 设置文字大小
     *
     */
    fun setTextSize(textSize: Float) {
        this.ma5Paint.textSize = textSize
        this.ma10Paint.textSize = textSize
    }


}
