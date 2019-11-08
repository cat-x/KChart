package xyz.a1api.kchart.draw

import android.content.Context
import android.graphics.*
import xyz.a1api.kchart.R
import xyz.a1api.kchart.entity.ICandle
import xyz.a1api.kchart.entity.IMinuteLine
import xyz.a1api.kchart.utils.dip2px
import xyz.a1api.kchart.utils.getResColor
import xyz.a1api.kchart.utils.sp
import xyz.a1api.kchart.view.BaseKChartView
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Cat-x on 2018/11/28.
 * For KChart
 * Cat-x All Rights Reserved
 */
class MinuteChartDraw constructor(rect: Rect, kChartView: BaseKChartView) :
    BaseChartDraw<IMinuteLine>(rect, kChartView) {

    private val mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mEntityPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mAvgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPricePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nowPriceLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nowPriceRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nowPricePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mTextSize = 10f
    private val mContext: Context

    init {
        mContext = mKChartView.context
        mTextSize = mContext.sp(mTextSize).toFloat()
//        mVolumePaintGreen.color = ContextCompat.getColor(context, R.color.chart_green)
//        mVolumePaintRed.color = ContextCompat.getColor(context, R.color.chart_red)

        mLinePaint.color = mContext.getResColor(R.color.minute_chart_line_color)
        mEntityPaint.color = mContext.getResColor(R.color.minute_chart_entity_color)

        mTextPaint.color = Color.parseColor("#B1B2B6")
        mTextPaint.textSize = mTextSize
        mTextPaint.strokeWidth = mContext.dip2px(0.5f).toFloat()
        mAvgPaint.color = Color.parseColor("#90A901")
        mAvgPaint.strokeWidth = mContext.dip2px(0.5f).toFloat()
        mAvgPaint.textSize = mTextSize
        mPricePaint.color = Color.parseColor("#FF6600")
        mPricePaint.strokeWidth = mContext.dip2px(0.5f).toFloat()
        mPricePaint.textSize = mTextSize

        nowPriceLinePaint.color = Color.parseColor("#4B85D6")
        nowPriceLinePaint.style = Paint.Style.STROKE
        nowPriceLinePaint.strokeWidth = 1f
        val pathEffect = DashPathEffect(floatArrayOf(21f, 7f, 21f, 7f), 1f)
        nowPriceLinePaint.pathEffect = pathEffect

        nowPriceRectPaint.color = Color.parseColor("#4B85D6")
        nowPriceRectPaint.strokeWidth = 1.5f
        nowPriceRectPaint.style = Paint.Style.STROKE

        nowPricePaint.color = Color.parseColor("#4B85D6")
        nowPricePaint.textSize = mContext.sp(11).toFloat()
    }

    override fun getMaxValue(point: IMinuteLine): Float {
        return if (point.isUseAvgPrice) {
            max(point.getAvgPrice(), point.getPrice())
        } else {
            point.getPrice()
        }
    }


    override fun getMinValue(point: IMinuteLine): Float {
        return if (point.isUseAvgPrice) {
            min(point.getAvgPrice(), point.getPrice())
        } else {
            point.getPrice()
        }
    }

    /**
     * 循环遍历显示区域的实体
     * 在[BaseChartDraw.drawCharts] 中被调用
     * @param curIndex 当前点的索引值
     * @param curPoint 当前点实体
     * @param lastPoint 上一个点实体
     */
    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: IMinuteLine,
        lastPoint: IMinuteLine,
        startX: Float,
        stopX: Float
    ) {
//        drawLine(canvas, if (curPoint.getPrice() <= lastPoint.getPrice()) mVolumePaintGreen else mVolumePaintRed, curIndex, curPoint.getPrice(), lastPoint.getPrice(),startX,stopX)//画K线
        //画K线
        drawLine(
            canvas,
            mLinePaint,
            curIndex,
            curPoint.getPrice(),
            lastPoint.getPrice(),
            startX,
            stopX
        )
        //画实体区域
        drawMainMinuteLine(
            canvas,
            mEntityPaint,
            curIndex,
            curPoint.getPrice(),
            lastPoint.getPrice(),
            startX,
            stopX
        )
    }

    /**
     * 画数值
     * 注意：在此方法画出来的图表不会缩放和平移
     * @param canvas [Canvas]
     * @param start 显示区域实体索引的开始
     * @param stop 显示区域实体索引的结束
     */
    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        var isInLastColumn = false
        if (mKChartView.getMinScrollX() != 0) {
            val textWidth =
                nowPricePaint.measureText((mKChartView.getAdapter().getData().last() as ICandle).getClosePrice().toString())
            isInLastColumn = mKChartView.mScrollX in mKChartView.getMinScrollX()..
                    (-textWidth).toInt()
        }
        if (!isInLastColumn) {
            val lastPrice = (mKChartView.getAdapter().getData().last() as ICandle).getClosePrice()
            val text = lastPrice.toString() + " ▶"
            val y1 = when {
                lastPrice > getMaxValue() -> getY(getMaxValue())
                lastPrice < getMinValue() -> getY(getMinValue())
                else -> getY(lastPrice)
            }


            val path = Path()

            val padding = mContext.dip2px(5).toFloat()
            val width = nowPricePaint.measureText(text)
            val metrics = nowPricePaint.fontMetrics
            val textHeight = metrics.descent - metrics.ascent
            val top = y1 - textHeight / 2 - padding / 2
            val bottom = y1 + textHeight / 2 + padding / 2
            val left = (mKChartView.mChartWidth.toFloat() - width) / 2 - padding * 4
            val r = RectF(left, top, left + width + padding * 2, bottom)
            canvas.drawRoundRect(r, padding * 2, padding * 2, nowPriceRectPaint)
            canvas.drawText(
                text,
                r.left + padding,
                y1 + (metrics.descent - metrics.ascent) / 2 - metrics.descent,
                nowPricePaint
            )

            path.moveTo(0f, y1)
            path.lineTo(r.left, y1)
            path.moveTo(r.right, y1)
            path.lineTo(mKChartView.mChartWidth.toFloat(), y1)
            canvas.drawPath(path, nowPriceLinePaint)
        } else {
            val lastPrice = (mKChartView.getAdapter().getData().last() as ICandle).getClosePrice()
            val text = lastPrice.toString()
            val y1 = getY(lastPrice)
            val metrics = nowPricePaint.fontMetrics
            val x1 = mKChartView.mChartWidth - nowPricePaint.measureText(text)
            canvas.drawText(
                text,
                x1,
                y1 + (metrics.descent - metrics.ascent) / 2 - metrics.descent,
                nowPricePaint
            )


            val path = Path()
            path.moveTo(
                mKChartView.translateXtoX(getX(mKChartView.getAdapter().getCount() - 1)),
                y1
            )
            path.lineTo(x1, y1)
            canvas.drawPath(path, nowPriceLinePaint)
        }
    }

}