package xyz.a1api.kchart.draw

import android.content.Context
import android.graphics.*
import android.util.Pair
import androidx.core.content.ContextCompat
import xyz.a1api.kchart.R
import xyz.a1api.kchart.base.IValueFormatter
import xyz.a1api.kchart.entity.ICandle
import xyz.a1api.kchart.entity.ICandleBoll
import xyz.a1api.kchart.formatter.ValueFormatter
import xyz.a1api.kchart.utils.*
import xyz.a1api.kchart.view.BaseKChartView
import java.util.*


/**
 * 主图的实现类
 * Created by tifezh on 2016/6/14.
 * For KChart
 * Cat-x All Rights Reserved
 */

class MainDraw constructor(rect: Rect, kChartView: BaseKChartView) :
    BaseChartDraw<ICandleBoll>(rect, kChartView) {

    private var mCandleWidth = 0f
    private var mCandleLineWidth = 0f
    private val mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGreenPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val ma1Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma3Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma4Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma5Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ma6Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private val nowPriceLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nowPriceRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nowPricePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mSelectorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSelectorBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mUpPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mMbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDnPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mContext: Context

    private var mCandleSolid = true
    var mainDrawIndicator: String = ICandleBoll.IMainConfig.useMainDrawIndicator

    private val context: Context by lazy {
        kChartView.context
    }

    init {
        val context = mKChartView.context
        mContext = context
        mRedPaint.color = ContextCompat.getColor(context, R.color.chart_red)
        mGreenPaint.color = ContextCompat.getColor(context, R.color.chart_green)

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

    override fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: ICandleBoll,
        lastPoint: ICandleBoll,
        startX: Float,
        stopX: Float
    ) {
        drawCandle(
            canvas,
            getX(curIndex),
            curPoint.getHighPrice(),
            curPoint.getLowPrice(),
            curPoint.getOpenPrice(),
            curPoint.getClosePrice()
        )

        when (mainDrawIndicator) {
            ICandleBoll.IMainConfig.typeMA -> {
                1.toBigDecimal() + 2.toBigDecimal()
                ICandle.ICandleConfig.customizeMALists.unfold { index, num, enable ->
                    if (enable) {
                        if (lastPoint.getMAbyNum(num) != 0f) {
                            drawLine(
                                canvas,
                                getPaint(index),
                                curIndex,
                                curPoint.getMAbyNum(num),
                                lastPoint.getMAbyNum(num),
                                startX,
                                stopX
                            )
                        }
                    }
                }
                /*      //画ma5
                      if (lastPoint.getMAbyNum(5) != 0f) {
                          drawLine(canvas, ma1Paint, curIndex, curPoint.getMAbyNum(5), lastPoint.getMAbyNum(5))
                      }
                      //画ma10
                      if (lastPoint.getMAbyNum(10) != 0f) {
                          drawLine(canvas, ma2Paint, curIndex, curPoint.getMAbyNum(10), lastPoint.getMAbyNum(10))
                      }
                      //画ma20
                      if (lastPoint.getMAbyNum(20) != 0f) {
                          drawLine(canvas, ma3Paint, curIndex, curPoint.getMAbyNum(20), lastPoint.getMAbyNum(20))
                      }*/
            }
            ICandleBoll.IMainConfig.typeBOLL -> {
                drawLine(
                    canvas,
                    mUpPaint,
                    curIndex,
                    curPoint.getUp(),
                    lastPoint.getUp(),
                    startX,
                    stopX
                )
                drawLine(
                    canvas,
                    mMbPaint,
                    curIndex,
                    curPoint.getMb(),
                    lastPoint.getMb(),
                    startX,
                    stopX
                )
                drawLine(
                    canvas,
                    mDnPaint,
                    curIndex,
                    curPoint.getDn(),
                    lastPoint.getDn(),
                    startX,
                    stopX
                )
            }
        }
    }

    private fun getPaint(index: Int): Paint {
        return when (index % 3) {
            0 -> ma1Paint
            1 -> ma2Paint
            2 -> ma3Paint
            3 -> ma4Paint
            4 -> ma5Paint
            5 -> ma6Paint
            else -> ma1Paint
        }
    }

    override fun drawValues(canvas: Canvas, start: Int, stop: Int) {
        val point = getDisplayItem()

        when (mainDrawIndicator) {
            ICandleBoll.IMainConfig.typeMA -> {
                CanvasUtils.drawTexts(
                    canvas,
                    mContext.dip2px(10f).toFloat(),
                    mContext.dip2px(5f).toFloat(),
                    XAlign.LEFT,
                    YAlign.BOTTOM,
                    *ICandle.ICandleConfig.customizeMALists.filter { it.value.enable }.map {
                        Pair(
                            getPaint(it.key),
                            "MA${it.value.name}:" + mKChartView.formatValue(point.getMAbyNum(it.value.name)) + " "
                        )
                    }.toTypedArray()
                    /*    Pair(ma1Paint, "MA5:" + mKChartView.formatValue(point.getMAbyNum(5)) + " "),
                        Pair(ma2Paint, "MA10:" + mKChartView.formatValue(point.getMAbyNum(10)) + " "),
                        Pair(ma3Paint, "MA20:" + mKChartView.formatValue(point.getMAbyNum(20)) + " ")*/
                )
            }
            ICandleBoll.IMainConfig.typeBOLL -> {
                CanvasUtils.drawTexts(
                    canvas,
                    mContext.dip2px(10f).toFloat(),
                    mContext.dip2px(5f).toFloat(),
                    XAlign.LEFT,
                    YAlign.BOTTOM,
                    Pair(mUpPaint, "UP:" + mKChartView.formatValue(point.getUp()) + " "),
                    Pair(mMbPaint, "MB:" + mKChartView.formatValue(point.getMb()) + " "),
                    Pair(mDnPaint, "DN:" + mKChartView.formatValue(point.getDn()) + " ")
                )
            }
        }


        drawNowPrice(canvas)

        if (mKChartView.isHadSelect) {
            drawSelector(mKChartView, canvas)
        }
    }

    private fun drawNowPrice(canvas: Canvas) {
        var isInLastColumn = false
        if (mKChartView.getMinScrollX() != 0) {
            val textWidth =
                nowPricePaint.measureText((mKChartView.getAdapter().getData().last() as ICandleBoll).getClosePrice().toString())
            isInLastColumn = mKChartView.mScrollX in mKChartView.getMinScrollX()..
                    (-textWidth).toInt()
        }
        if (!isInLastColumn) {
            val lastPrice =
                (mKChartView.getAdapter().getData().last() as ICandleBoll).getClosePrice()
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
            val lastPrice =
                (mKChartView.getAdapter().getData().last() as ICandleBoll).getClosePrice()
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

    public override fun getMaxValue(point: ICandleBoll): Float {
        return when (mainDrawIndicator) {
            ICandleBoll.IMainConfig.typeMA -> {
                Math.max(point.getHighPrice(), point.getMAbyMaxNum())
            }
            ICandleBoll.IMainConfig.typeBOLL -> {
                Math.max(
                    point.getHighPrice(),
                    if (java.lang.Float.isNaN(point.getUp())) point.getMb() else point.getUp()
                )
            }
            else -> {
                point.getHighPrice()
            }
        }
    }

    public override fun getMinValue(point: ICandleBoll): Float {
        return when (mainDrawIndicator) {
            ICandleBoll.IMainConfig.typeMA -> {
                Math.min(point.getMAbyMinNum(), point.getLowPrice())
            }
            ICandleBoll.IMainConfig.typeBOLL -> {
                Math.min(
                    point.getHighPrice(),
                    if (java.lang.Float.isNaN(point.getDn())) point.getMb() else point.getDn()
                )
            }
            else -> {
                point.getLowPrice()
            }
        }
    }

    override fun getValueFormatter(): IValueFormatter {
        return ValueFormatter()
    }

    /**
     * 画蜡烛（Candle）图
     * @param canvas
     * @param x      x轴坐标
     * @param high   最高价
     * @param low    最低价
     * @param open   开盘价
     * @param close  收盘价
     */
    private fun drawCandle(
        canvas: Canvas,
        x: Float,
        high: Float,
        low: Float,
        open: Float,
        close: Float
    ) {
        val _high = getY(high)
        val _low = getY(low)
        val _open = getY(open)
        val _close = getY(close)
        val r = mCandleWidth / 2
        val lineR = mCandleLineWidth / 2
        when {
            _open > _close -> {
                if (mCandleSolid) {//实心
                    canvas.drawRect(x - r, _close, x + r, _open, mRedPaint)
                    canvas.drawRect(x - lineR, _high, x + lineR, _low, mRedPaint)
                } else {
                    mRedPaint.strokeWidth = mCandleLineWidth
                    canvas.drawLine(x, _high, x, _close, mRedPaint)
                    canvas.drawLine(x, _open, x, _low, mRedPaint)
                    canvas.drawLine(x - r + lineR, _open, x - r + lineR, _close, mRedPaint)
                    canvas.drawLine(x + r - lineR, _open, x + r - lineR, _close, mRedPaint)
                    mRedPaint.strokeWidth = mCandleLineWidth * mKChartView.scaleX
                    canvas.drawLine(x - r, _open, x + r, _open, mRedPaint)
                    canvas.drawLine(x - r, _close, x + r, _close, mRedPaint)
                }
            }
            _open < _close -> {
                if (mCandleSolid) {//实心
                    canvas.drawRect(x - r, _open, x + r, _close, mGreenPaint)
                    canvas.drawRect(x - lineR, _high, x + lineR, _low, mGreenPaint)
                } else {
                    mGreenPaint.strokeWidth = mCandleLineWidth
                    canvas.drawLine(x, _high, x, _open, mGreenPaint)
                    canvas.drawLine(x, _close, x, _low, mGreenPaint)
                    canvas.drawLine(x - r + lineR, _close, x - r + lineR, _open, mGreenPaint)
                    canvas.drawLine(x + r - lineR, _close, x + r - lineR, _open, mGreenPaint)
                    mGreenPaint.strokeWidth = mCandleLineWidth * mKChartView.scaleX
                    canvas.drawLine(x - r, _close, x + r, _close, mGreenPaint)
                    canvas.drawLine(x - r, _open, x + r, _open, mGreenPaint)
                }
            }
            else -> {
                canvas.drawRect(x - r, _open, x + r, _close + 1, mRedPaint)
                canvas.drawRect(x - lineR, _high, x + lineR, _low, mRedPaint)
            }
        }
    }

    /**
     * draw选择器
     * @param view
     * @param canvas
     */
    private fun drawSelector(view: BaseKChartView, canvas: Canvas) {
        val metrics = mSelectorTextPaint.fontMetrics
        val textHeight = metrics.descent - metrics.ascent

        var index = view.getSelectedIndex()
        if (index < 0) {
            index = 0
        }
        val padding = mContext.dip2px(3f).toFloat()
        val margin = mContext.dip2px(5f).toFloat()
        var width = 0f
        val left: Float
        val top = margin + view.topPadding
        val height = padding * 11 + textHeight * 8

        val point = view.getItem(index) as ICandleBoll
        val strings = ArrayList<kotlin.Pair<String, Any>>()
        strings.add(
            context.getString(R.string.kchart_info_time) to view.formatDateTime(
                view.getAdapter().getDate(
                    index
                )
            )
        )
        strings.add(context.getString(R.string.kchart_info_open_price) to point.getOpenPrice())
        strings.add(context.getString(R.string.kchart_info_hign_price) to point.getHighPrice())
        strings.add(context.getString(R.string.kchart_info_low_price) to point.getLowPrice())
        strings.add(context.getString(R.string.kchart_info_close_price) to point.getClosePrice())
        strings.add(
            context.getString(R.string.kchart_info_rise_fall_num) to (point.getClosePrice() - point.getOpenPrice()).toFix(
                2
            )
        )
        strings.add(context.getString(R.string.kchart_info_quote_change) to ((point.getClosePrice() - point.getOpenPrice()) / point.getOpenPrice()).toPercentage())
        strings.add(context.getString(R.string.kchart_info_volume) to point.getClosePrice())

        for (s in strings) {
            width = Math.max(width, mSelectorTextPaint.measureText(s.first + s.second + padding))
        }
        width += padding * 2

        val x = view.translateXtoX(view.getX(index))
        if (x > view.mChartWidth / 2) {
            left = margin
        } else {
            left = view.mChartWidth.toFloat() - width - margin
        }

        val r = RectF(left, top, left + width, top + height)
        canvas.drawRoundRect(r, padding, padding, mSelectorBackgroundPaint)
        var y = top + padding * 2 + (textHeight - metrics.bottom - metrics.top) / 2
//        val y1 = y -padding
        for (s in strings) {
            canvas.drawText(s.first, left + padding, y, mSelectorTextPaint)
            canvas.drawText(
                s.second.toString(),
                mSelectorTextPaint.getXAlignRight(left + width - padding, s.second.toString()),
                y,
                mSelectorTextPaint
            )
            y += textHeight + padding
        }

//        y = mMainDraw.getY(point.getClosePrice())+ mMainRect.top
//        val x1: Float
//        if (x > view.getChartWidth() / 2) {
//            x1 = 0f
//            canvas.drawRect(x1, y1, width,  y, mBackgroundPaint)
//        } else {
//            x1 = view.getChartWidth() - width - padding
//            canvas.drawRect(x1, y1, x1 + width, y, mBackgroundPaint)
//        }

    }

    /**
     * 设置蜡烛宽度
     * @param candleWidth
     */
    fun setCandleWidth(candleWidth: Float) {
        mCandleWidth = candleWidth
    }

    /**
     * 设置蜡烛线宽度
     * @param candleLineWidth
     */
    fun setCandleLineWidth(candleLineWidth: Float) {
        mCandleLineWidth = candleLineWidth
    }

    /**
     * 设置背景颜色
     */
    fun setTextBackgroundColor(color: Int) {
        mBackgroundPaint.color = color
    }

    /**
     * 设置ma1颜色
     * @param color
     */
    fun setMa1Color(color: Int) {
        this.ma1Paint.color = color
    }

    /**
     * 设置ma2颜色
     * @param color
     */
    fun setMa2Color(color: Int) {
        this.ma2Paint.color = color
    }

    /**
     * 设置ma3颜色
     * @param color
     */
    fun setMa3Color(color: Int) {
        this.ma3Paint.color = color
    }

    /**
     * 设置ma3颜色
     * @param color
     */
    fun setMa4Color(color: Int) {
        this.ma4Paint.color = color
    }

    /**
     * 设置ma3颜色
     * @param color
     */
    fun setMa5Color(color: Int) {
        this.ma5Paint.color = color
    }

    /**
     * 设置ma3颜色
     * @param color
     */
    fun setMa6Color(color: Int) {
        this.ma6Paint.color = color
    }

    /**
     * 设置选择器文字颜色
     * @param color
     */
    fun setSelectorTextColor(color: Int) {
        mSelectorTextPaint.color = color
    }

    /**
     * 设置选择器文字大小
     * @param textSize
     */
    fun setSelectorTextSize(textSize: Float) {
        mSelectorTextPaint.textSize = textSize
    }

    /**
     * 设置选择器背景
     * @param color
     */
    fun setSelectorBackgroundColor(color: Int) {
        mSelectorBackgroundPaint.color = color
    }

    /**
     * 设置曲线宽度
     */
    fun setMALineWidth(width: Float) {
        ma6Paint.strokeWidth = width
        ma5Paint.strokeWidth = width
        ma4Paint.strokeWidth = width
        ma3Paint.strokeWidth = width
        ma2Paint.strokeWidth = width
        ma1Paint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setMATextSize(textSize: Float) {
        ma6Paint.textSize = textSize
        ma5Paint.textSize = textSize
        ma4Paint.textSize = textSize
        ma3Paint.textSize = textSize
        ma2Paint.textSize = textSize
        ma1Paint.textSize = textSize
    }

    /**
     * 蜡烛是否实心
     */
    fun setCandleSolid(candleSolid: Boolean) {
        mCandleSolid = candleSolid
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
    fun setBOLLLineWidth(width: Float) {
        mUpPaint.strokeWidth = width
        mMbPaint.strokeWidth = width
        mDnPaint.strokeWidth = width
    }

    /**
     * 设置文字大小
     */
    fun setBOLLTextSize(textSize: Float) {
        mUpPaint.textSize = textSize
        mMbPaint.textSize = textSize
        mDnPaint.textSize = textSize
    }
}
