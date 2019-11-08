package xyz.a1api.kchart.base

import android.graphics.Canvas

/**
 * 可绘制基类
 * Created by tifezh on 2018/4/3.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IDraw {

    /**
     * 开始绘制
     */
    fun draw(canvas: Canvas)

}
