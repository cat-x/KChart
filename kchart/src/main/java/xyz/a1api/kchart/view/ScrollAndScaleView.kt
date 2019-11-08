package xyz.a1api.kchart.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.OverScroller
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat

/**
 * 可以滑动和放大的view
 * Created by tian on 2016/5/3.
 * For KChart
 * Cat-x All Rights Reserved
 */
abstract class ScrollAndScaleView : RelativeLayout, GestureDetector.OnGestureListener,
    ScaleGestureDetector.OnScaleGestureListener {

    protected open lateinit var mDetector: GestureDetectorCompat
    protected open lateinit var mScaleDetector: ScaleGestureDetector
    private lateinit var mScroller: OverScroller

    open var mScrollX = 0
        protected set

    /** 当前缩放值*/
    protected open var mScaleX = 1f

    /** 缩放的最大值*/
    open var mScaleXMax = 2f

    /** 缩放的最小值*/
    open var mScaleXMin = 0.5f

    /** 是否长按*/
    open var isLongPress = false
        protected set

    /** 是否选中*/
    open var isHadSelect = false
        protected set

    protected open var touch = false

    private var mMultipleTouch = false

    /** 能否滑动*/
    private var mScrollEnable = true

    /** 是否可以缩放*/
    private var mScaleEnable = true


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
        mScroller = OverScroller(context)
//        setOnTouchListener { _, _ ->
//            if (isHadSelect){
//                return@setOnTouchListener true
//            }
//            return@setOnTouchListener false
//        }
    }


    /**
     * 计算选中坐标点
     */
    abstract fun calculateSelectedX(x: Float)

    /**
     * 滑到了最左边
     */
    abstract fun onLeftSide()

    /**
     * 滑到了最右边
     */
    abstract fun onRightSide()


    /**
     * 获取位移的最小值
     */
    abstract fun getMinScrollX(): Int

    /**
     * 获取位移的最大值
     */
    abstract fun getMaxScrollX(): Int

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
        if (!isLongPress && !isMultipleTouch()) {
            scrollBy(Math.round(distanceX), 0)
            return true
        }
        return false
    }

    override fun onLongPress(event: MotionEvent) {
        isLongPress = true
        isHadSelect = true
        calculateSelectedX(event.x)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (!isTouch() && isScrollEnable()) {
            mScroller.fling(
                mScrollX, 0, Math.round(velocityX / mScaleX), 0,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                0, 0
            )
        }
        return true
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (!isTouch()) {
                scrollTo(mScroller.currX, mScroller.currY)
            } else {
                mScroller.forceFinished(true)
            }
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        scrollTo(mScrollX - Math.round(x / mScaleX), 0)
    }

    override fun scrollTo(x: Int, y: Int) {
        if (!isScrollEnable()) {
            mScroller.forceFinished(true)
            return
        }
        val oldX = mScrollX
        mScrollX = x
        if (mScrollX < getMinScrollX()) {
            mScrollX = getMinScrollX()
            onRightSide()
            mScroller.forceFinished(true)
        } else if (mScrollX > getMaxScrollX()) {
            mScrollX = getMaxScrollX()
            onLeftSide()
            mScroller.forceFinished(true)
        }
        onScrollChanged(mScrollX, 0, oldX, 0)
        invalidate()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (!isScaleEnable()) {
            return false
        }
        val oldScale = mScaleX
        mScaleX *= detector.scaleFactor
        when {
            mScaleX < mScaleXMin -> mScaleX = mScaleXMin
            mScaleX > mScaleXMax -> mScaleX = mScaleXMax
            else -> onScaleChanged(mScaleX, oldScale)
        }
        return true
    }

    protected open fun onScaleChanged(scale: Float, oldScale: Float) {
        invalidate()
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                touch = true
                isHadSelect = true
                calculateSelectedX(event.x)
            }
            MotionEvent.ACTION_MOVE -> if (event.pointerCount == 1) {
                //长按之后移动
                if (isLongPress) {
                    onLongPress(event)
                } else {
                    isHadSelect = false
                }
            } else {
                isHadSelect = false
            }
            MotionEvent.ACTION_POINTER_UP -> invalidate()
            MotionEvent.ACTION_UP -> {
                isLongPress = false
                touch = false
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                isLongPress = false
                touch = false
                invalidate()
            }
        }
        mMultipleTouch = event.pointerCount > 1
        this.mDetector.onTouchEvent(event)
        this.mScaleDetector.onTouchEvent(event)
        return true
    }

    /**
     * 是否在触摸中
     *
     * @return
     */
    open fun isTouch(): Boolean {
        return touch
    }

    /**
     * 设置ScrollX
     *
     * @param scrollX
     */
    override fun setScrollX(scrollX: Int) {
        this.mScrollX = scrollX
        scrollTo(scrollX, 0)
    }

    /**
     * 是否是多指触控
     * @return
     */
    open fun isMultipleTouch(): Boolean {
        return mMultipleTouch
    }

    protected open fun checkAndFixScrollX() {
        if (mScrollX < getMinScrollX()) {
            mScrollX = getMinScrollX()
            mScroller.forceFinished(true)
        } else if (mScrollX > getMaxScrollX()) {
            mScrollX = getMaxScrollX()
            mScroller.forceFinished(true)
        }
    }

    open fun isScrollEnable(): Boolean {
        return mScrollEnable
    }


    /**
     * 设置是否可以滑动
     */
    open fun setScrollEnable(scrollEnable: Boolean) {
        mScrollEnable = scrollEnable
    }

    open fun isScaleEnable(): Boolean {
        return mScaleEnable
    }

    /**
     * 设置是否可以缩放
     */
    open fun setScaleEnable(scaleEnable: Boolean) {
        mScaleEnable = scaleEnable
    }

    /**
     * X坐标系的缩放值
     */
    override fun getScaleX(): Float {
        return mScaleX
    }
}
