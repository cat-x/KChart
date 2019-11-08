package xyz.a1api.kchart.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Pair


/**
 * 画图工具类
 * Created by tifezh on 2018/4/1.
 * For KChart
 * Cat-x All Rights Reserved
 */

@Suppress("MemberVisibilityCanBePrivate")
object CanvasUtils {

    fun getTextX(_x: Float, paint: Paint, text: String, align: XAlign): Float {
        var x = _x
        when (align) {
            XAlign.CENTER -> x -= paint.measureText(text) / 2
            XAlign.RIGHT -> x -= paint.measureText(text)
        }
        return x
    }

    fun getTextY(_y: Float, paint: Paint, align: YAlign): Float {
        var y = _y
        val fm = paint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val baseLine = (textHeight - fm.bottom - fm.top) / 2
        when (align) {
            YAlign.TOP -> y += baseLine
            YAlign.CENTER -> y += textHeight / 2 - fm.descent
            YAlign.BOTTOM -> y += baseLine - textHeight
        }
        return y
    }


    /**
     * 绘制文本工具
     * @param canvas [Canvas]
     * @param paint 画笔
     * @param x x坐标
     * @param y y坐标
     * @param xAlign x轴方向对齐方式
     * @param yAlign y轴方向对齐方式
     */
    fun drawText(
        canvas: Canvas,
        paint: Paint,
        _x: Float,
        _y: Float,
        text: String,
        xAlign: XAlign,
        yAlign: YAlign
    ) {
        var x = _x
        var y = _y
        y = getTextY(y, paint, yAlign)
        x = getTextX(x, paint, text, xAlign)
        canvas.drawText(text, x, y, paint)
    }


    /**
     * 绘制多个文本
     * @param canvas [Canvas]
     * @param x x坐标
     * @param y y坐标
     * @param xAlign x轴方向对齐方式
     * @param yAlign y轴方向对齐方式
     * @param strings 画笔和对应文本的集合
     */
    @SafeVarargs
    fun drawTexts(
        canvas: Canvas,
        _x: Float,
        y: Float,
        xAlign: XAlign,
        yAlign: YAlign,
        vararg strings: Pair<Paint, String>
    ) {
        var x = _x
        if (strings.isEmpty()) {
            return
        }

        var textWidth = 0f
        if (xAlign != XAlign.LEFT) {
            for (s in strings) {
                textWidth += s.first.measureText(s.second)
            }

            if (xAlign == XAlign.CENTER) x -= textWidth / 2f
            if (xAlign == XAlign.RIGHT) x -= textWidth
        }

        for (s in strings) {
            val newY = getTextY(y, s.first, yAlign)
            canvas.drawText(s.second, x, newY, s.first)
            x += s.first.measureText(s.second)
        }
    }
}
