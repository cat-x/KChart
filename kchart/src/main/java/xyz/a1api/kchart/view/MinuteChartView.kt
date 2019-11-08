package xyz.a1api.kchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import xyz.a1api.kchart.R
import xyz.a1api.kchart.base.IValueFormatter
import xyz.a1api.kchart.entity.IMinuteLine
import xyz.a1api.kchart.formatter.BigValueFormatter
import xyz.a1api.kchart.utils.DateUtil
import java.util.*

/**
 * 分时图
 * 简单的分时图示例 更丰富的需求可能需要在此基础上再作修改
 */
class MinuteChartView : View, GestureDetector.OnGestureListener {

    private var mHeight = 0
    private var mWidth = 0
    private var mVolumeHeight = 100
    private var mTopPadding = 15
    private var mBottomPadding = 15
    private val mGridRows = 6
    private val GridColumns = 5
    private val mAvgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPricePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mVolumePaintRed = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mVolumePaintGreen = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBackgroundColor: Int = 0
    private var mValueMin: Float = 0.toFloat()
    private var mValueMax: Float = 0.toFloat()
    private var mVolumeMax: Float = 0.toFloat()
    private var mValueStart: Float = 0.toFloat()
    private var mScaleY = 1f
    private var mVolumeScaleY = 1f
    private var mTextSize = 10f
    private var isLongPress = false
    private var selectedIndex: Int = 0
    private lateinit var mDetector: GestureDetectorCompat
    private val mPoints = ArrayList<IMinuteLine>()
    private var mFirstStartTime: Date? = null
    private var mFirstEndTime: Date? = null
    private var mSecondStartTime: Date? = null
    private var mSecondEndTime: Date? = null
    private var mTotalTime: Long = 0
    private var mPointWidth: Float = 0.toFloat()

    private var mVolumeFormatter: IValueFormatter? = null

    /**
     * 获取最大能有多少个点
     */
    private val maxPointCount: Long
        get() = mTotalTime / ONE_MINUTE

    /**
     * 获取点的个数
     */
    private val itemSize: Int
        get() = mPoints.size

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mDetector = GestureDetectorCompat(context, this)
        mTopPadding = dp2px(mTopPadding.toFloat())
        mBottomPadding = dp2px(mBottomPadding.toFloat())
        mTextSize = sp2px(mTextSize).toFloat()
        mVolumeHeight = dp2px(mVolumeHeight.toFloat())
        mGridPaint.color = Color.parseColor("#353941")
        mGridPaint.strokeWidth = dp2px(1f).toFloat()
        mTextPaint.color = Color.parseColor("#B1B2B6")
        mTextPaint.textSize = mTextSize
        mTextPaint.strokeWidth = dp2px(0.5f).toFloat()
        mAvgPaint.color = Color.parseColor("#90A901")
        mAvgPaint.strokeWidth = dp2px(0.5f).toFloat()
        mAvgPaint.textSize = mTextSize
        mPricePaint.color = Color.parseColor("#FF6600")
        mPricePaint.strokeWidth = dp2px(0.5f).toFloat()
        mPricePaint.textSize = mTextSize
        mVolumePaintGreen.color = ContextCompat.getColor(context, R.color.chart_green)
        mVolumePaintRed.color = ContextCompat.getColor(context, R.color.chart_red)
        mBackgroundColor = Color.parseColor("#202326")
        mBackgroundPaint.color = mBackgroundColor

        mVolumeFormatter = BigValueFormatter()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.mDetector.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE ->
                //一个点的时候滑动
                if (event.pointerCount == 1) {
                    //长按之后移动
                    if (isLongPress) {
                        calculateSelectedX(event.x)
                        invalidate()
                    }
                }
            MotionEvent.ACTION_UP -> {
                isLongPress = false
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                isLongPress = false
                invalidate()
            }
        }
        return true
    }

    private fun calculateSelectedX(x: Float) {
        selectedIndex = (x * 1f / getX(mPoints.size - 1) * (mPoints.size - 1) + 0.5f).toInt()
        if (selectedIndex < 0) {
            selectedIndex = 0
        }
        if (selectedIndex > mPoints.size - 1) {
            selectedIndex = mPoints.size - 1
        }
    }

    /**
     * 根据索引获取x的值
     */
    private fun getX(position: Int): Float {
        val date = /*mPoints[position].getDate()*/Date()
        return if (mSecondStartTime != null && date.time >= mSecondStartTime!!.time) {
            1f * (date.time - mSecondStartTime!!.time + 60000 +
                    mFirstEndTime!!.time - mFirstStartTime!!.time) / mTotalTime * (mWidth - mPointWidth) + mPointWidth / 2f
        } else {
            1f * (date.time - mFirstStartTime!!.time) / mTotalTime * (mWidth - mPointWidth) + mPointWidth / 2f
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val height = h - mTopPadding - mBottomPadding
        this.mHeight = height - mVolumeHeight
        this.mWidth = w
        notifyChanged()
    }

    /**
     * @param data 数据源
     * @param startTime       显示的开始时间
     * @param endTime         显示的结束时间
     * @param yesClosePrice 昨日开盘价
     */
    fun initData(
        data: Collection<IMinuteLine>,
        startTime: Date,
        endTime: Date,
        yesClosePrice: Float
    ) {
        initData(data, startTime, endTime, null, null, yesClosePrice)
    }

    /**
     * @param data 数据源
     * @param startTime       显示的开始时间
     * @param endTime         显示的结束时间
     * @param firstEndTime    休息开始时间 可空
     * @param secondStartTime 休息结束时间 可空
     * @param yesClosePrice 昨收价
     */
    fun initData(
        data: Collection<IMinuteLine>?,
        startTime: Date,
        endTime: Date,
        firstEndTime: Date?,
        secondStartTime: Date?,
        yesClosePrice: Float
    ) {
        this.mFirstStartTime = startTime
        this.mSecondEndTime = endTime
        if (mFirstStartTime!!.time >= mSecondEndTime!!.time) throw IllegalStateException("开始时间不能大于结束时间")
        mTotalTime = mSecondEndTime!!.time - mFirstStartTime!!.time
        if (firstEndTime != null && secondStartTime != null) {
            this.mFirstEndTime = firstEndTime
            this.mSecondStartTime = secondStartTime
            if (!(mFirstStartTime!!.time < mFirstEndTime!!.time &&
                        mFirstEndTime!!.time < mSecondStartTime!!.time &&
                        mSecondStartTime!!.time < mSecondEndTime!!.time)
            ) {
                throw IllegalStateException("时间区间有误")
            }
            mTotalTime -= mSecondStartTime!!.time - mFirstEndTime!!.time - 60000
        }
        setValueStart(yesClosePrice)
        if (data != null) {
            mPoints.clear()
            this.mPoints.addAll(data)
        }
        notifyChanged()
    }

    /**
     * 当数据发生变化时调用
     */
    fun notifyChanged() {
        mValueMax = java.lang.Float.MIN_VALUE
        mValueMin = java.lang.Float.MAX_VALUE
        for (i in mPoints.indices) {
            val point = mPoints[i]
            mValueMax = Math.max(mValueMax, point.getPrice())
            mValueMin = Math.min(mValueMin, point.getPrice())
            mVolumeMax = Math.max(mVolumeMax, point.getVolume())
        }
        //最大值和开始值的差值
        val offsetValueMax = mValueMax - mValueStart
        val offsetValueMin = mValueStart - mValueMin
        //以开始的点为中点值   上下间隙多出20%
        val offset =
            (if (offsetValueMax > offsetValueMin) offsetValueMax else offsetValueMin) * 1.2f
        //坐标轴高度以开始的点对称
        mValueMax = mValueStart + offset
        mValueMin = mValueStart - offset
        //y轴的缩放值
        mScaleY = mHeight / (mValueMax - mValueMin)
        //判断最大值和最小值是否一致
        if (mValueMax == mValueMin) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mValueMax += Math.abs(mValueMax * 0.05f)
            mValueMin -= Math.abs(mValueMax * 0.05f)
            if (mValueMax == 0f) {
                mValueMax = 1f
            }
        }

        if (mVolumeMax == 0f) {
            mVolumeMax = 1f
        }

        mVolumeMax *= 1.1f
        //成交量的缩放值
        mVolumeScaleY = mVolumeHeight / mVolumeMax
        mPointWidth = mWidth.toFloat() / maxPointCount
        mVolumePaintRed.strokeWidth = mPointWidth * 0.8f
        mVolumePaintGreen.strokeWidth = mPointWidth * 0.8f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackgroundColor)
        if (mWidth == 0 || mHeight == 0 || mPoints == null || mPoints.size == 0) {
            return
        }
        drawGird(canvas)
        if (mPoints.size > 0) {
            var lastPoint = mPoints[0]
            var lastX = getX(0)
            for (i in mPoints.indices) {
                val curPoint = mPoints[i]
                val curX = getX(i)
                canvas.drawLine(
                    lastX,
                    getY(lastPoint.getPrice()),
                    curX,
                    getY(curPoint.getPrice()),
                    mPricePaint
                )
                canvas.drawLine(
                    lastX,
                    getY(lastPoint.getAvgPrice()),
                    curX,
                    getY(curPoint.getAvgPrice()),
                    mAvgPaint
                )
                //成交量
                val volumePaint =
                    if (i == 0 && curPoint.getPrice() <= mValueStart || curPoint.getPrice() <= lastPoint.getPrice()) mVolumePaintGreen else mVolumePaintRed
                canvas.drawLine(
                    curX,
                    getVolumeY(0f),
                    curX,
                    getVolumeY(curPoint.getVolume()),
                    volumePaint
                )
                lastPoint = curPoint
                lastX = curX
            }
        }
        drawText(canvas)
        //画指示线
        if (isLongPress) {
            val point = mPoints[selectedIndex]
            var x = getX(selectedIndex)
            canvas.drawLine(x, 0f, x, (mHeight + mVolumeHeight).toFloat(), mTextPaint)
            canvas.drawLine(
                0f,
                getY(point.getPrice()),
                mWidth.toFloat(),
                getY(point.getPrice()),
                mTextPaint
            )
            //画指示线的时间
            var text = DateUtil.timeFormat.format(Date()/*point.getDate()*/)
            x = x - mTextPaint.measureText(text) / 2
            if (x < 0) {
                x = 0f
            }
            if (x > mWidth - mTextPaint.measureText(text)) {
                x = mWidth - mTextPaint.measureText(text)
            }
            val fm = mTextPaint.fontMetrics
            val textHeight = fm.descent - fm.ascent
            val baseLine = (textHeight - fm.bottom - fm.top) / 2
            //下方时间
            canvas.drawRect(
                x,
                mHeight + mVolumeHeight - baseLine + textHeight,
                x + mTextPaint.measureText(text),
                mVolumeHeight.toFloat() + mHeight.toFloat() + baseLine,
                mBackgroundPaint
            )
            canvas.drawText(
                text,
                x,
                mHeight.toFloat() + mVolumeHeight.toFloat() + baseLine,
                mTextPaint
            )

            val r = textHeight / 2
            val y = getY(point.getPrice())
            //左方值
            text = floatToString(point.getPrice())
            canvas.drawRect(0f, y - r, mTextPaint.measureText(text), y + r, mBackgroundPaint)
            canvas.drawText(text, 0f, fixTextY(y), mTextPaint)
            //右方值
            text = floatToString((point.getPrice() - mValueStart) * 100f / mValueStart) + "%"
            canvas.drawRect(
                mWidth - mTextPaint.measureText(text),
                y - r,
                mWidth.toFloat(),
                y + r,
                mBackgroundPaint
            )
            canvas.drawText(text, mWidth - mTextPaint.measureText(text), fixTextY(y), mTextPaint)
        }
        drawValue(canvas, if (isLongPress) selectedIndex else mPoints.size - 1)
    }

    /**
     * 画值
     */
    private fun drawValue(canvas: Canvas, index: Int) {
        val fm = mTextPaint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val baseLine = (textHeight - fm.bottom - fm.top) / 2
        if (index >= 0 && index < mPoints.size) {
            val y = baseLine - textHeight
            val point = mPoints[index]
            var text = "成交价:" + floatToString(point.getPrice()) + " "
            var x = 0f
            canvas.drawText(text, x, y, mPricePaint)
            x += mPricePaint.measureText(text)
            text = "均价:" + floatToString(point.getAvgPrice()) + " "
            canvas.drawText(text, x, y, mAvgPaint)
            //成交量
            text = "VOL:" + mVolumeFormatter!!.format(point.getVolume())
            canvas.drawText(
                text,
                mWidth - mTextPaint.measureText(text),
                mHeight + baseLine,
                mTextPaint
            )
        }
    }

    /**
     * 修正y值
     */
    private fun getY(value: Float): Float {
        return (mValueMax - value) * mScaleY
    }

    private fun getVolumeY(value: Float): Float {
        return (mVolumeMax - value) * mVolumeScaleY + mHeight
    }

    private fun drawGird(canvas: Canvas) {
        //先画出坐标轴
        canvas.translate(0f, mTopPadding.toFloat())
        canvas.scale(1f, 1f)
        //横向的grid
        val rowSpace = (mHeight / mGridRows).toFloat()

        for (i in 0..mGridRows) {
            canvas.drawLine(0f, rowSpace * i, mWidth.toFloat(), rowSpace * i, mGridPaint)
        }
        canvas.drawLine(
            0f,
            rowSpace * mGridRows / 2,
            mWidth.toFloat(),
            rowSpace * mGridRows / 2,
            mGridPaint
        )

        canvas.drawLine(
            0f,
            (mHeight + mVolumeHeight).toFloat(),
            mWidth.toFloat(),
            (mHeight + mVolumeHeight).toFloat(),
            mGridPaint
        )
        //纵向的grid
        val columnSpace = (mWidth / GridColumns).toFloat()
        for (i in 0..GridColumns) {
            canvas.drawLine(
                columnSpace * i,
                0f,
                columnSpace * i,
                (mHeight + mVolumeHeight).toFloat(),
                mGridPaint
            )
        }

    }

    /**
     * 解决text居中的问题
     */
    fun fixTextY(y: Float): Float {
        val fontMetrics = mTextPaint.fontMetrics
        return y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    private fun drawText(canvas: Canvas) {
        val fm = mTextPaint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val baseLine = (textHeight - fm.bottom - fm.top) / 2
        //画左边的值
        canvas.drawText(floatToString(mValueMax), 0f, baseLine, mTextPaint)
        canvas.drawText(floatToString(mValueMin), 0f, mHeight.toFloat(), mTextPaint)
        val rowValue = (mValueMax - mValueMin) / mGridRows
        val rowSpace = (mHeight / mGridRows).toFloat()
        for (i in 0..mGridRows) {
            val text = floatToString(rowValue * (mGridRows - i) + mValueMin)
            if (i >= 1 && i < mGridRows) {
                canvas.drawText(text, 0f, fixTextY(rowSpace * i), mTextPaint)
            }
        }
        var text = floatToString((mValueMax - mValueStart) * 100f / mValueStart) + "%"
        canvas.drawText(text, mWidth - mTextPaint.measureText(text), baseLine, mTextPaint)
        text = floatToString((mValueMin - mValueStart) * 100f / mValueStart) + "%"
        canvas.drawText(text, mWidth - mTextPaint.measureText(text), mHeight.toFloat(), mTextPaint)
        for (i in 0..mGridRows) {
            text =
                floatToString((rowValue * (mGridRows - i) + mValueMin - mValueStart) * 100f / mValueStart) + "%"
            if (i >= 1 && i < mGridRows) {
                canvas.drawText(
                    text,
                    mWidth - mTextPaint.measureText(text),
                    fixTextY(rowSpace * i),
                    mTextPaint
                )
            }
        }
        //画时间
        val y = mHeight.toFloat() + mVolumeHeight.toFloat() + baseLine
        canvas.drawText(DateUtil.timeFormat.format(mFirstStartTime), 0f, y, mTextPaint)
        canvas.drawText(
            DateUtil.timeFormat.format(mSecondEndTime),
            mWidth - mTextPaint.measureText(DateUtil.timeFormat.format(mSecondEndTime)),
            y,
            mTextPaint
        )
        //成交量
        canvas.drawText(mVolumeFormatter!!.format(mVolumeMax), 0f, mHeight + baseLine, mTextPaint)
    }

    fun dp2px(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 保留2位小数
     */
    fun floatToString(value: Float): String {
        var s = String.format("%.2f", value)
        var end = s[s.length - 1]
        while (s.contains(".") && (end == '0' || end == '.')) {
            s = s.substring(0, s.length - 1)
            end = s[s.length - 1]
        }
        return s
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        isLongPress = true
        calculateSelectedX(e.x)
        invalidate()
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    /**
     * 设置开始的值 对称轴线
     */
    fun setValueStart(valueStart: Float) {
        this.mValueStart = valueStart
    }

    /**
     * 修改某个点的值
     * @param position 索引值
     */
    fun changePoint(position: Int, point: IMinuteLine) {
        mPoints[position] = point
        notifyChanged()
    }

    /**
     * 刷新最后一个点
     */
    fun refreshLastPoint(point: IMinuteLine) {
        changePoint(itemSize - 1, point)
    }

    /**
     * 添加一个点
     */
    fun addPoint(point: IMinuteLine) {
        mPoints.add(point)
        notifyChanged()
    }

    /**
     * 根据索引获取点
     */
    fun getItem(position: Int): IMinuteLine {
        return mPoints[position]
    }

    /**
     * 设置成交量格式化器
     * @param volumeFormatter [IValueFormatter] 成交量格式化器
     */
    fun setVolumeFormatter(volumeFormatter: IValueFormatter) {
        mVolumeFormatter = volumeFormatter
    }

    companion object {

        private val ONE_MINUTE = 60000
    }
}
