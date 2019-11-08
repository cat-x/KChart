package xyz.a1api.kchart.base

import android.graphics.Canvas

/**
 * 画图的基类 根据实体来画图形
 * @param <T> 实体类泛型
 * Created by tifezh on 2018/3/29.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IChartDraw<T> : IDraw {

    /**
     * 获取绘制区域的顶部
     */
    fun getTop(): Int;

    /**
     * 获取绘制区域的左部
     */
    fun getLeft(): Int

    /**
     * 获取绘制区域的右部
     */
    fun getRight(): Int

    /**
     * 获取绘制区域的底部
     */
    fun getBottom(): Int

    /**
     * 获取绘制高度
     */
    fun getHeight(): Int

    /**
     * 获取绘制宽度
     */
    fun getWidth(): Int

    /**
     * 在绘制之前计算
     * @param start 显示区域实体索引的开始
     * @param stop 显示区域实体索引的结束
     */
    fun calculate(start: Int, stop: Int)

    /**
     * 画图表
     * 注意：在此方法画出来的图表会缩放和平移
     * @param canvas [Canvas]
     * @param start 显示区域实体索引的开始
     * @param stop 显示区域实体索引的结束
     */
    fun drawCharts(canvas: Canvas, start: Int, stop: Int)

    /**
     * 画数值
     * 注意：在此方法画出来的图表不会缩放和平移
     * @param canvas [Canvas]
     * @param start 显示区域实体索引的开始
     * @param stop 显示区域实体索引的结束
     */
    fun drawValues(canvas: Canvas, start: Int, stop: Int)

    /**
     * 获取当前显示区域实体中最大的值
     */
    fun getMaxValue(): Float

    /**
     * 获取当前显示区域实体中最小的值
     */
    fun getMinValue(): Float

    /**
     * 将数值转换为坐标值
     */
    fun getY(value: Float): Float

    /**
     * 获取格式化器
     */
    fun getValueFormatter(): IValueFormatter
}