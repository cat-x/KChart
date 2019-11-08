package xyz.a1api.kchart.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import androidx.annotation.CallSuper
import xyz.a1api.kchart.base.IChartDraw
import xyz.a1api.kchart.base.IValueFormatter
import xyz.a1api.kchart.view.BaseKChartView

/**
 * 图形绘制基类
 * Created by tifezh on 2018/3/30.
 * For KChart
 * Cat-x All Rights Reserved
 */


/**
 * @param mRect 显示区域
 * @param mKChartView BaseKChartView
 */
@SuppressWarnings("WeakerAccess", "unused")
abstract class BaseChartDraw<T> constructor(var mRect: Rect, var mKChartView: BaseKChartView) :
    IChartDraw<T> {

    var titleSplitValue: Float = 30f

    /**可视区域中的最大值*/
    private var mMaxValue: Float = 0.toFloat()

    /**可视区域中的最小值*/
    private var mMinValue: Float = 0.toFloat()

    /**可视区域中的Y坐标缩放值*/
    private var mScaleY: Float = 0.toFloat()


    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(mKChartView.translateX(), getTop().toFloat() + titleSplitValue)
        canvas.scale(mKChartView.scaleX, 1f)
        drawCharts(canvas, mKChartView.getStartIndex(), mKChartView.getStopIndex())
        canvas.restore()
        canvas.save()
        canvas.translate(0f, getTop().toFloat())
        drawValues(canvas, mKChartView.getStartIndex(), mKChartView.getStopIndex())
        canvas.restore()
    }

    @CallSuper
    override fun calculate(start: Int, stop: Int) {
        mMaxValue = java.lang.Float.MIN_VALUE
        mMinValue = java.lang.Float.MAX_VALUE
        val data = getData()
        for (i in start..stop) {
            val point = data[i]
            foreachCalculate(i, point)
        }
        //        if(mMaxValue!=mMinValue) {
        //            float padding = (mMaxValue - mMinValue) * 0.05f;
        //            mMaxValue += padding;
        //            mMinValue -= padding;
        //        } else {
        //            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
        //            mMaxValue += 1f;
        //            mMinValue -= 1f;
        //            if (mMaxValue == 0) {
        //                mMaxValue = 1;
        //            }
        //        }

        mScaleY = (mRect.height() - titleSplitValue) * 1f / (mMaxValue - mMinValue)
    }

    @CallSuper
    override fun drawCharts(canvas: Canvas, start: Int, stop: Int) {
        val data = getData()
        for (index in start..stop) {
            val currentPoint = data[index]
            val lastPoint = if (index == start) currentPoint else data[index - 1]
            val lastX = getX(index - 1)
            val currentX = getX(index)
//            Log.i("xxx", "startX: " + lastX)
//            Log.i("xxx", " stopX: " + currentX)
            foreachDrawChart(canvas, index, currentPoint, lastPoint, lastX, currentX)
        }
    }

    /**
     * 循环遍历显示区域的实体
     * 在[BaseChartDraw.calculate]} 中被循环调用
     * @param index 索引
     * @param point 数据实体
     */
    @Suppress("UNUSED_PARAMETER")
    @CallSuper
    protected fun foreachCalculate(index: Int, point: T) {
        mMaxValue = Math.max(mMaxValue, getMaxValue(point))
        mMinValue = Math.min(mMinValue, getMinValue(point))
    }

    /**
     * 循环遍历显示区域的实体
     * 在[BaseChartDraw.drawCharts] 中被调用
     * @param curIndex 当前点的索引值
     * @param curPoint 当前点实体
     * @param lastPoint 上一个点实体
     */
    protected abstract fun foreachDrawChart(
        canvas: Canvas,
        curIndex: Int,
        curPoint: T,
        lastPoint: T,
        startX: Float,
        stopX: Float
    )

    override fun getMaxValue(): Float {
        return mMaxValue
    }

    override fun getMinValue(): Float {
        return mMinValue
    }

    /**
     * 获取当前实体中最大的值
     *
     * @param point 当前实体
     */
    protected abstract fun getMaxValue(point: T): Float

    /**
     * 获取当前实体中最小的值
     *
     * @param point 实体
     */
    protected abstract fun getMinValue(point: T): Float

    override fun getTop(): Int {
        return mRect.top
    }

    override fun getBottom(): Int {
        return mRect.bottom
    }

    override fun getLeft(): Int {
        return mRect.left
    }

    override fun getRight(): Int {
        return mRect.right
    }

    override fun getHeight(): Int {
        return mRect.height()
    }

    override fun getWidth(): Int {
        return mRect.width()
    }

    override fun getY(value: Float): Float {
        return (mMaxValue - value) * mScaleY
    }

    /**
     * 获取全部数据集合
     */
    @Suppress("UNCHECKED_CAST")
    protected fun getData(): List<T> {
        return mKChartView.getAdapter().getData() as List<T>
    }

    /**
     * 获取实体个数
     */
    protected fun getDataCount(): Int {
        return mKChartView.getAdapter().getCount()
    }

    /**
     * 根据索引索取x坐标
     * @param index 索引值
     * @see BaseKChartView.getX
     */
    fun getX(index: Int): Float {
        return mKChartView.getX(index)
    }

    /**
     * 画线
     * @param curIndex 当前点索引
     * @param curValue 当前点的值
     * @param lastValue 前一个点的值
     */
    fun drawLine(
        canvas: Canvas,
        paint: Paint,
        curIndex: Int,
        curValue: Float,
        lastValue: Float,
        startX: Float,
        stopX: Float
    ) {
        //如果是第一个点就不用画线
        if (curIndex != mKChartView.getStartIndex()) {
            canvas.drawLine(startX, getY(lastValue), stopX, getY(curValue), paint)
        }
    }

    /**
     * 画矩形
     * @param curIndex 当前点的index
     * @param width 矩形的宽度
     * @param topValue 上方的值
     * @param bottomValue 底部的值
     */
    fun drawRect(
        canvas: Canvas,
        paint: Paint,
        curIndex: Int,
        width: Float,
        topValue: Float,
        bottomValue: Float
    ) {
        val _topValue = getY(topValue)
        val _bottomValue = getY(bottomValue)
        val x = getX(curIndex)
        canvas.drawRect(x - width / 2f, _topValue, x + width / 2f, _bottomValue, paint)
    }

    /**
     * 在主区域画分时线
     *
     * @param curIndex 当前点索引
     * @param curValue 当前点的值
     * @param lastValue 前一个点的值
     */
    fun drawMainMinuteLine(
        canvas: Canvas,
        paint: Paint,
        curIndex: Int,
        curValue: Float,
        lastValue: Float,
        startX: Float,
        stopX: Float
    ) {
        //如果是第一个点就不用画线
        if (curIndex != mKChartView.getStartIndex()) {
            val startY = getY(lastValue)
            val stopY = getY(curValue)
            val path5 = Path()
            path5.moveTo(startX - 0.3f, mRect.height() - titleSplitValue)
            path5.lineTo(startX - 0.3f, startY)
            path5.lineTo(stopX, stopY)
            path5.lineTo(stopX, mRect.height() - titleSplitValue)
            path5.close()
            canvas.drawPath(path5, paint)
        }

    }

    /**
     * 获取显示的值
     * 长按状态下显示长按的值
     * 非长按状态下显示最右边的值
     */
    @Suppress("UNCHECKED_CAST")
    fun getDisplayItem(): T {
        return mKChartView.getItem(if (mKChartView.isHadSelect) mKChartView.getSelectedIndex() else mKChartView.getStopIndex()) as T
    }

    override fun getValueFormatter(): IValueFormatter {
        return mKChartView.getValueFormatter()
    }
}
