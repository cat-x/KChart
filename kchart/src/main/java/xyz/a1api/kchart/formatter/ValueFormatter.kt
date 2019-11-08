package xyz.a1api.kchart.formatter

import xyz.a1api.kchart.base.IValueFormatter

/**
 * Value格式化类
 * Created by tifezh on 2016/6/21.
 * For KChart
 * Cat-x All Rights Reserved
 */

class ValueFormatter : IValueFormatter {
    override fun format(value: Float): String {
        return String.format("%.2f", value)
    }
}
