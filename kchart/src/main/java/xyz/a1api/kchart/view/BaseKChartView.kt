package xyz.a1api.kchart.view

import android.animation.ValueAnimator
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.GestureDetectorCompat
import xyz.a1api.kchart.R
import xyz.a1api.kchart.base.IAdapter
import xyz.a1api.kchart.base.IChartDraw
import xyz.a1api.kchart.base.IDateTimeFormatter
import xyz.a1api.kchart.base.IValueFormatter
import xyz.a1api.kchart.entity.ICandle
import xyz.a1api.kchart.entity.IKLine
import xyz.a1api.kchart.formatter.TimeFormatter
import xyz.a1api.kchart.formatter.ValueFormatter
import xyz.a1api.kchart.utils.dip2px
import xyz.a1api.kchart.utils.getXAlignRight
import java.util.*

/**
 * k线图
 * Created by tian on 2016/5/3.
 * For KChart
 * Cat-x All Rights Reserved
 */
abstract class BaseKChartView : ScrollAndScaleView {

    protected var isRefreshing = false

    /** 选择绘制的子图*/
    var _secondDrawIndicator: String = ""

    private var mTranslateX = java.lang.Float.MIN_VALUE

    /** View绘制可用宽度*/
    var mChartWidth = 0
        private set

    private var mTopPadding: Int = 0

    private var mBottomPadding: Int = 0

    private var mDataLen = 0f

    private var mStartIndex = 0

    private var mStopIndex = 0

    private var mPointWidth = 6f

    /** 网格行数*/
    private var mGridRows = 4

    /** 网格列数*/
    private var mGridColumns = 4

    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mTimeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mSelectedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mSelectedIndex: Int = 0

    private lateinit var mMainDraw: IChartDraw<*>

    private lateinit var mVolumeDraw: IChartDraw<*>

    protected var isShowChildDraw = true


    private var mAdapter: IAdapter<*>? = null

    private val mDataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            mItemCount = getAdapter().getCount()
            notifyChanged()
        }

        override fun onInvalidated() {
            mItemCount = getAdapter().getCount()
            notifyChanged()
        }
    }
    //当前点的个数
    private var mItemCount: Int = 0
    private var mChildDraw: IChartDraw<*>? = null
    private val mChildDraws = ArrayList<Pair<String, IChartDraw<*>>>()

    private var mValueFormatter: IValueFormatter = ValueFormatter()
    private var mDateTimeFormatter: IDateTimeFormatter? = null


    private var mAnimator: ValueAnimator? = null

    private val mAnimationDuration: Long = 500

    private var mOverScrollRange = 0f

    private var mOnSelectedChangedListener: OnSelectedChangedListener? = null

    protected open val mMainRect = Rect()

    private val mTabRect = Rect()

    protected open val mChildVolRect = Rect()

    protected open val mChildRect = Rect()

    private var mLineWidth: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setWillNotDraw(false)
        mDetector = GestureDetectorCompat(context, this)
        mScaleDetector = ScaleGestureDetector(context, this)
        mTopPadding = resources.getDimension(R.dimen.chart_top_padding).toInt()
        mBottomPadding = resources.getDimension(R.dimen.chart_bottom_padding).toInt()


        mAnimator = ValueAnimator.ofFloat(0f, 1f)
        mAnimator?.duration = mAnimationDuration
        mAnimator?.addUpdateListener { invalidate() }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.mChartWidth = w
        setOverScrollRange(mChartWidth / mGridColumns.toFloat())
        if (mScrollX == 0) {
            mScrollX = -(mChartWidth / mGridColumns.toFloat() - 0.5).toInt()
        }
        initRect(w, h)
//        mKChartTabView.translationY = mMainRect.bottom.toFloat()
        setTranslateXFromScrollX(mScrollX)
    }

    private fun initRect(w: Int = width, h: Int = height) {

        val displayHeight = h - mTopPadding - mBottomPadding
        val displayWidth = mChartWidth

        val mMainHeight = (displayHeight * if (isShowChildDraw) 0.66f else (0.66f + 0.17f)).toInt()
        val mChildHeight = (displayHeight * 0.17f).toInt()
        val mChildHeight2 = if (isShowChildDraw) (displayHeight * 0.17f).toInt() else 0
//        val mChildHeight = (displayHeight * 0.25f).toInt()
        mMainRect.set(0, mTopPadding, displayWidth, mTopPadding + mMainHeight)
//        mTabRect.set(0, mMainRect.bottom, mChartWidth, mMainRect.bottom + mMainChildSpace)
        mChildVolRect.set(0, mMainRect.bottom, displayWidth, mMainRect.bottom + mChildHeight)
        mChildRect.set(0, mChildVolRect.bottom, displayWidth, mChildVolRect.bottom + mChildHeight2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackgroundPaint.color)
        if (mChartWidth == 0 || mMainRect.height() == 0 || mItemCount == 0) {
            return
        }
        calculateValue()
        drawGird(canvas)
        mMainDraw.draw(canvas)
        mVolumeDraw.draw(canvas)
        if (isShowChildDraw) {
            mChildDraw?.draw(canvas)
        }
        //画选择线
        if (isHadSelect) {
            val point = getItem(mSelectedIndex) as ICandle
            val x = translateXtoX(getX(mSelectedIndex))
            val y = mMainDraw.getY(point.getClosePrice()) + mMainRect.top
            canvas.drawLine(
                x,
                mMainRect.top.toFloat(),
                x,
                mMainRect.bottom.toFloat(),
                mSelectedLinePaint
            )
            canvas.drawLine(0f, y, mChartWidth.toFloat(), y, mSelectedLinePaint)
            canvas.drawLine(
                x,
                mChildVolRect.top.toFloat(),
                x,
                mChildVolRect.bottom.toFloat(),
                mSelectedLinePaint
            )
            canvas.drawLine(
                x,
                mChildRect.top.toFloat(),
                x,
                mChildRect.bottom.toFloat(),
                mSelectedLinePaint
            )
            val mSelectedPointPaint = Paint(Paint.ANTI_ALIAS_FLAG);
            mSelectedPointPaint.strokeWidth = mSelectedLinePaint.strokeWidth * 3
            mSelectedPointPaint.color = mSelectedLinePaint.color
            canvas.drawPoint(x, y, mSelectedPointPaint)
        }
        drawText(canvas)
    }

    /**
     * 解决text居中的问题
     */
    open fun fixTextY(y: Float): Float {
        val fontMetrics = mTextPaint.fontMetrics
        return y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    /**
     * 画表格
     * @param canvas
     */
    private fun drawGird(canvas: Canvas) {
        //-----------------------上方k线图------------------------
        //横向的grid
        val rowSpace = (mMainRect.height() / mGridRows).toFloat()
        for (i in 0..mGridRows) {
            canvas.drawLine(
                0f,
                rowSpace * i + mMainRect.top,
                mChartWidth.toFloat(),
                rowSpace * i + mMainRect.top,
                mGridPaint
            )
        }
        //-----------------------下方子图------------------------
        canvas.drawLine(
            0f,
            mChildRect.top.toFloat(),
            mChartWidth.toFloat(),
            mChildRect.top.toFloat(),
            mGridPaint
        )
        canvas.drawLine(
            0f,
            mChildRect.bottom.toFloat(),
            mChartWidth.toFloat(),
            mChildRect.bottom.toFloat(),
            mGridPaint
        )

//        canvas.drawLine(0f, mChildVolRect.top.toFloat(), mChartWidth.toFloat(), mChildVolRect.top.toFloat(), mGridPaint)
//        canvas.drawLine(0f, mChildVolRect.bottom.toFloat(), mChartWidth.toFloat(), mChildVolRect.bottom.toFloat(), mGridPaint)
        //纵向的grid
        val columnSpace = (mChartWidth / mGridColumns).toFloat()
        for (i in 0..mGridColumns) {
            canvas.drawLine(
                columnSpace * i,
                mMainRect.top.toFloat(),
                columnSpace * i,
                mMainRect.bottom.toFloat(),
                mGridPaint
            )
            canvas.drawLine(
                columnSpace * i,
                mChildVolRect.top.toFloat(),
                columnSpace * i,
                mChildVolRect.bottom.toFloat(),
                mGridPaint
            )
            canvas.drawLine(
                columnSpace * i,
                mChildRect.top.toFloat(),
                columnSpace * i,
                mChildRect.bottom.toFloat(),
                mGridPaint
            )
        }
    }

    /**
     * 画文字
     * @param canvas
     */
    private fun drawText(canvas: Canvas) {
        val fm = mTextPaint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val baseLine = (textHeight - fm.bottom - fm.top) / 2
        //--------------画上方k线图的值-------------
        if (mMainDraw != null) {
            canvas.drawText(
                formatValue(mMainDraw.getMaxValue()),
                mTextPaint.getXAlignRight(
                    mChartWidth,
                    formatValue(mMainDraw.getMaxValue())
                ) - context.dip2px(5f),
                baseLine + mMainRect.top, mTextPaint
            )
            canvas.drawText(
                formatValue(mMainDraw.getMinValue()),
                mTextPaint.getXAlignRight(
                    mChartWidth,
                    formatValue(mMainDraw.getMinValue())
                ) - context.dip2px(5f),
                mMainRect.bottom - textHeight + baseLine,
                mTextPaint
            )
            val rowValue = (mMainDraw.getMaxValue() - mMainDraw.getMinValue()) / mGridRows
            val rowSpace = (mMainRect.height() / mGridRows).toFloat()
            for (i in 1 until mGridRows) {
                val text = formatValue(rowValue * (mGridRows - i) + mMainDraw.getMinValue())
                canvas.drawText(
                    text,
                    mTextPaint.getXAlignRight(mChartWidth, text) - context.dip2px(5f),
                    fixTextY(rowSpace * i + mMainRect.top), mTextPaint
                )
            }
        }

        mVolumeDraw.also {
            //            CanvasUtils.drawText(canvas, mTextPaint, 0f, mChildVolRect.top + baseLine, it.getValueFormatter().format(it.getMaxValue()), XAlign.RIGHT,YAlign.TOP)
            canvas.drawText(
                it.getValueFormatter().format(it.getMaxValue()),
                mTextPaint.getXAlignRight(
                    mChartWidth,
                    it.getValueFormatter().format(it.getMaxValue())
                ) - context.dip2px(5f),
                mChildVolRect.top + baseLine,
                mTextPaint
            )

//     成交量不需要最小值
//            canvas.drawText(
//                it.getValueFormatter().format(it.getMinValue()),
//                mTextPaint.getXAlignRight(mChartWidth, it.getValueFormatter().format(it.getMinValue())) - context.dip2px(5f),
//                mChildVolRect.bottom.toFloat(),
//                mTextPaint
//            )
        }


        //--------------画下方子图的值-------------
        if (isShowChildDraw) {
            mChildDraw?.also {
                canvas.drawText(
                    it.getValueFormatter().format(it.getMaxValue()),
                    mTextPaint.getXAlignRight(
                        mChartWidth,
                        it.getValueFormatter().format(it.getMaxValue())
                    ) - context.dip2px(5f),
                    mChildRect.top + baseLine,
                    mTextPaint
                )
                canvas.drawText(
                    it.getValueFormatter().format(it.getMinValue()),
                    mTextPaint.getXAlignRight(
                        mChartWidth,
                        it.getValueFormatter().format(it.getMinValue())
                    ) - context.dip2px(5f),
                    mChildRect.bottom.toFloat(),
                    mTextPaint
                )
            }
        }

        //--------------画时间---------------------
        val columnSpace = (mChartWidth / mGridColumns).toFloat()
        var y = mChildRect.bottom + baseLine

        val startX = getX(mStartIndex) - mPointWidth / 2
        val stopX = getX(mStopIndex) + mPointWidth / 2

        //分割线下方时间
        for (i in 1 until mGridColumns) {
            val translateX = xToTranslateX(columnSpace * i)
            if (translateX in startX..stopX) {
                val index = indexOfTranslateX(translateX)
                val text = formatDateTime(mAdapter?.getDate(index))
                canvas.drawText(
                    text,
                    columnSpace * i - mTimeTextPaint.measureText(text) / 2,
                    y,
                    mTimeTextPaint
                )
            }
        }

        //最左边时间
        var translateX = xToTranslateX(0f)
        if (translateX >= startX && translateX <= stopX) {
            val time = formatDateTime(getAdapter().getDate(mStartIndex))
            canvas.drawText(time, -mTextPaint.measureText(time) / 2, y, mTimeTextPaint)
        }
        //最右边时间
//        translateX = xToTranslateX(mChartWidth.toFloat())
//        if (translateX >= startX && translateX <= stopX) {
//            val text = formatDateTime(getAdapter().getDate(mStopIndex))
//            canvas.drawText(text, mChartWidth - mTextPaint.measureText(text), y, mTimeTextPaint)
//        }

        if (isHadSelect) {
            val point = getItem(mSelectedIndex) as IKLine
            val text = formatValue(point.getClosePrice())
            val r = textHeight / 2
            y = mMainDraw.getY(point.getClosePrice()) + mMainRect.top
            val x: Float
            if (translateXtoX(getX(mSelectedIndex)) < mChartWidth / 2) {
                x = 0f
                canvas.drawRect(x, y - r, mTextPaint.measureText(text), y + r, mBackgroundPaint)
            } else {
                x = mChartWidth - mTextPaint.measureText(text)
                canvas.drawRect(x, y - r, mChartWidth.toFloat(), y + r, mBackgroundPaint)
            }
            canvas.drawText(text, x, fixTextY(y), mTextPaint)
        }
    }

    open fun dp2px(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    open fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 格式化值
     */
    open fun formatValue(value: Float): String {
        return getValueFormatter().format(value)
    }

    /**
     * 重新计算并刷新线条
     */
    open fun notifyChanged() {
        if (mItemCount != 0) {
            mDataLen = (mItemCount - 1) * mPointWidth
            checkAndFixScrollX()
            setTranslateXFromScrollX(mScrollX)
        } else {
            scrollX = 0
        }
        invalidate()
    }

    override fun calculateSelectedX(x: Float) {
        //如果刷新，则不进行计算
        if (isRefreshing || getAdapter().getData().isEmpty()) {
            return
        }
        val lastIndex = mSelectedIndex
        mSelectedIndex = indexOfTranslateX(xToTranslateX(x))
        if (mSelectedIndex < mStartIndex) {
            mSelectedIndex = mStartIndex
        }
        if (mSelectedIndex > mStopIndex) {
            mSelectedIndex = mStopIndex
        }
        if (lastIndex != mSelectedIndex) {
            onSelectedChanged(this, getItem(mSelectedIndex), mSelectedIndex)
        }
    }

    override fun onLongPress(event: MotionEvent) {
        super.onLongPress(event)
        invalidate()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        setTranslateXFromScrollX(mScrollX)
    }

    override fun onScaleChanged(scale: Float, oldScale: Float) {
        checkAndFixScrollX()
        setTranslateXFromScrollX(mScrollX)
        super.onScaleChanged(scale, oldScale)
    }

    /**
     * 计算当前的显示区域
     */
    private fun calculateValue() {
        if (!isHadSelect) {
            mSelectedIndex = -1
        }
        mStartIndex = indexOfTranslateX(xToTranslateX(0f))
        mStopIndex = indexOfTranslateX(xToTranslateX(mChartWidth.toFloat()))

        mMainDraw.calculate(mStartIndex, mStopIndex)
        mVolumeDraw.calculate(mStartIndex, mStopIndex)
        if (isShowChildDraw) {
            mChildDraw?.calculate(mStartIndex, mStopIndex)
        }

        mAnimator?.let {
            if (it.isRunning) {
                val value = it.animatedValue as Float
                mStopIndex = mStartIndex + Math.round(value * (mStopIndex - mStartIndex))
            }
        }

    }

    /**
     * 获取平移的最小值
     * @return
     */
    private fun getMinTranslateX(): Float {
        return if (!isFullScreen()) {
            getMaxTranslateX()
        } else -mDataLen + mChartWidth / mScaleX - mPointWidth / 2
    }

    /**
     * 获取平移的最大值
     * @return
     */
    private fun getMaxTranslateX(): Float {
        return mPointWidth / 2
    }

    override fun getMinScrollX(): Int {
        return (-(mOverScrollRange / mScaleX)).toInt()
    }

    override fun getMaxScrollX(): Int {
        return Math.round(getMaxTranslateX() - getMinTranslateX())
    }

    open fun indexOfTranslateX(translateX: Float): Int {
        return indexOfTranslateX(translateX, 0, mItemCount - 1)
    }

    /**
     * 根据索引获取实体
     * @param position 索引值
     * @return
     */
    open fun getItem(_position: Int): Any {
        var position = _position
        if (position < 0) {
            position = 0
        }
        return mAdapter?.getItem(position) ?: throw NullPointerException("mAdapter can't be null")
    }

    /**
     * 根据索引索取x坐标
     * @param position 索引值
     */
    open fun getX(position: Int): Float {
        return position * mPointWidth
    }

    /**
     * 获取适配器
     * @return
     */
    open fun getAdapter(): IAdapter<*> {
        return requireNotNull(mAdapter) { "mAdapter is can't be null" }
    }

    /**
     * 设置子图的绘制方法
     * @param position
     */
    private fun setChildDraw(position: Int) {
        secondDrawIndicator = mChildDraws[position].first
    }

    var secondDrawIndicator: String
        get() {
            return _secondDrawIndicator
        }
        set(value) {
            if (value.isEmpty()) {
                isShowChildDraw = false
            } else {
                mChildDraws.forEachIndexed { index, pair ->
                    if (pair.first == value) {
                        if (isShowChildDraw && _secondDrawIndicator == value) {
                            //如果已经显示，同时显示的子图就是当前，则不再次重绘
                            return
                        }
                        isShowChildDraw = true
                        this.mChildDraw = pair.second
                        _secondDrawIndicator = value
                    }
                }
            }
            initRect();
            invalidate()
        }

    open fun setSecondDraw(name: String) {

    }

    /**
     * 给子区域添加画图方法
     * @param name 显示的文字标签
     * @param childDraw IChartDraw
     */
    open fun addChildDraw(name: String, childDraw: IChartDraw<*>) {
        mChildDraws.add(name to childDraw)
    }

    /**
     * scrollX 转换为 TranslateX
     * @param scrollX
     */
    private fun setTranslateXFromScrollX(scrollX: Int) {
        mTranslateX = scrollX + getMinTranslateX()
    }

    /**
     * 获取ValueFormatter
     * @return
     */
    open fun getValueFormatter(): IValueFormatter {
        return mValueFormatter
    }

    /**
     * 设置ValueFormatter
     * @param valueFormatter value格式化器
     */
    open fun setValueFormatter(valueFormatter: IValueFormatter) {
        this.mValueFormatter = valueFormatter
    }

    /**
     * 获取DatetimeFormatter
     * @return 时间格式化器
     */
    open fun getDateTimeFormatter(): IDateTimeFormatter? {
        return mDateTimeFormatter
    }

    /**
     * 设置dateTimeFormatter
     * @param dateTimeFormatter 时间格式化器
     */
    open fun setDateTimeFormatter(dateTimeFormatter: IDateTimeFormatter) {
        mDateTimeFormatter = dateTimeFormatter
    }

    /**
     * 格式化时间
     * @param date
     */
    open fun formatDateTime(date: String?): String {
        return getDateTimeFormatter()?.format(date)
            ?: { setDateTimeFormatter(TimeFormatter()); TimeFormatter().format(date) }()
    }

    /**
     * 获取主区域的 IChartDraw
     * @return IChartDraw
     */
    open fun getMainDraw(): IChartDraw<*> {
        return mMainDraw
    }

    /**
     * 设置主区域的 IChartDraw
     * @param mainDraw IChartDraw
     */
    open fun setMainDraw(mainDraw: IChartDraw<*>) {
        mMainDraw = mainDraw
    }

    open fun setVolumeDraw(volumeDraw: IChartDraw<*>) {
        mVolumeDraw = volumeDraw
    }


    /**
     * 二分查找当前值的index
     * @return
     */
    open fun indexOfTranslateX(translateX: Float, start: Int, end: Int): Int {
        if (end == start) {
            return start
        }
        if (end - start == 1) {
            val startValue = getX(start)
            val endValue = getX(end)
            return if (Math.abs(translateX - startValue) < Math.abs(translateX - endValue)) start else end
        }
        val mid = start + (end - start) / 2
        val midValue = getX(mid)
        return if (translateX < midValue) {
            indexOfTranslateX(translateX, start, mid)
        } else if (translateX > midValue) {
            indexOfTranslateX(translateX, mid, end)
        } else {
            mid
        }
    }

    /**
     * 设置数据适配器
     */
    open fun setAdapter(adapter: IAdapter<*>) {
        if (mAdapter != null) {
            mAdapter?.unregisterDataSetObserver(mDataSetObserver)
        }
        mAdapter = adapter
        if (mAdapter != null) {
            mAdapter?.registerDataSetObserver(mDataSetObserver)
            mItemCount = mAdapter?.getCount() ?: 0
        } else {
            mItemCount = 0
        }
        notifyChanged()
    }

    /**
     * 开始动画
     */
    open fun startAnimation() {
        mAnimator?.start()
    }

    /**
     * 设置动画时间
     */
    open fun setAnimationDuration(duration: Long) {
        mAnimator?.duration = duration
    }

    /**
     * 设置表格行数
     */
    open fun setGridRows(gridRows: Int) {
        if (gridRows < 1) {
            error("gridRows must be greaterOrEqualTo 1")
        }
        mGridRows = gridRows
    }

    /**
     * 设置表格列数
     */
    open fun setGridColumns(gridColumns: Int) {
        if (gridColumns < 1) {
            error("gridColumns must be greaterOrEqualTo 1")
        }
        mGridColumns = gridColumns
    }

    /**
     * view中的x转化为TranslateX
     * @param x
     * @return
     */
    open fun xToTranslateX(x: Float): Float {
        return -mTranslateX + x / mScaleX
    }

    /**
     * translateX转化为view中的x
     * @param translateX
     * @return
     */
    open fun translateXtoX(translateX: Float): Float {
        return (translateX + mTranslateX) * mScaleX
    }

    /**
     * 获取上方padding
     */
    open fun getTopPadding(): Float {
        return mTopPadding.toFloat()
    }


    /**
     * 是否长按
     */
    open fun isLongPressM(): Boolean {
        return isLongPress
    }


    /**
     * 获取选择索引
     */
    open fun getSelectedIndex(): Int {
        return mSelectedIndex
    }

    open fun getChildRect(): Rect {
        return mChildRect
    }

    /**
     * 设置选择监听
     */
    open fun setOnSelectedChangedListener(l: OnSelectedChangedListener) {
        this.mOnSelectedChangedListener = l
    }

    open fun onSelectedChanged(view: BaseKChartView, point: Any, index: Int) {
        this.mOnSelectedChangedListener?.onSelectedChanged(view, point, index)
    }

    /**
     * 数据是否充满屏幕
     *
     * @return
     */
    open fun isFullScreen(): Boolean {
        return mDataLen >= mChartWidth / mScaleX
    }

    /**
     * 设置超出右方后可滑动的范围
     */
    open fun setOverScrollRange(_overScrollRange: Float) {
        var overScrollRange = _overScrollRange
        if (overScrollRange < 0) {
            overScrollRange = 0f
        }
        mOverScrollRange = overScrollRange
    }

    /**
     * 设置上方padding
     * @param topPadding
     */
    open fun setTopPadding(topPadding: Int) {
        mTopPadding = topPadding
    }

    /**
     * 设置下方padding
     * @param bottomPadding
     */
    open fun setBottomPadding(bottomPadding: Int) {
        mBottomPadding = bottomPadding
    }

    /**
     * 设置表格线宽度
     */
    open fun setGridLineWidth(width: Float) {
        mGridPaint.strokeWidth = width
    }

    /**
     * 设置表格线颜色
     */
    open fun setGridLineColor(color: Int) {
        mGridPaint.color = color
    }

    /**
     * 设置选择线宽度
     */
    open fun setSelectedLineWidth(width: Float) {
        mSelectedLinePaint.strokeWidth = width
    }

    /**
     * 设置表格线颜色
     */
    open fun setSelectedLineColor(color: Int) {
        mSelectedLinePaint.color = color
    }

    /**
     * 设置文字颜色
     */
    open fun setTextColor(color: Int) {
        mTextPaint.color = color
    }

    /**
     * 设置文字大小
     */
    open fun setTextSize(textSize: Float) {
        mTextPaint.textSize = textSize
    }

    open fun setTimeTextColor(color: Int) {
        mTimeTextPaint.color = color
    }

    open fun setTimeTextSize(textSize: Float) {
        mTimeTextPaint.textSize = textSize
    }

    /**
     * 设置背景颜色
     */
    override fun setBackgroundColor(color: Int) {
        mBackgroundPaint.color = color
    }


    /**
     * 选中点变化时的监听
     */
    interface OnSelectedChangedListener {
        /**
         * 当选点中变化时
         * @param view  当前view
         * @param point 选中的点
         * @param index 选中点的索引
         */
        fun onSelectedChanged(view: BaseKChartView, point: Any, index: Int)
    }

    /**
     * 获取文字大小
     */
    open fun getTextSize(): Float {
        return mTextPaint.textSize
    }

    /**
     * 获取曲线宽度
     */
    open fun getLineWidth(): Float {
        return mLineWidth
    }

    /**
     * 设置曲线的宽度
     */
    open fun setLineWidth(lineWidth: Float) {
        mLineWidth = lineWidth
    }

    /**
     * 设置每个点的宽度
     */
    open fun setPointWidth(pointWidth: Float) {
        mPointWidth = pointWidth
    }

    open fun getGridPaint(): Paint {
        return mGridPaint
    }

    open fun getTextPaint(): Paint {
        return mTextPaint
    }

    open fun getBackgroundPaint(): Paint {
        return mBackgroundPaint
    }

    open fun getSelectedLinePaint(): Paint {
        return mSelectedLinePaint
    }

    /**
     * 绘制区域显示的开始索引值
     */
    open fun getStartIndex(): Int {
        return mStartIndex
    }

    /**
     * 绘制区域显示的结束索引值
     */
    open fun getStopIndex(): Int {
        return mStopIndex
    }

    open fun getTranslateX(): Float {
        return mTranslateX
    }

    open fun translateX(): Float {
        return mScaleX * mTranslateX
    }

}
