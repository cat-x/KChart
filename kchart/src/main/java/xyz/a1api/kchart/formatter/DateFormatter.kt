package xyz.a1api.kchart.formatter

import xyz.a1api.kchart.base.IDateTimeFormatter
import xyz.a1api.kchart.utils.DateUtil

/**
 * 时间格式化器
 * Created by tifezh on 2016/6/21.
 * For KChart
 * Cat-x All Rights Reserved
 */

class DateFormatter : IDateTimeFormatter {
    override fun format(date: String?): String {
        return if (date.isNullOrBlank()) {
            ""
        } else {
            DateUtil.dateFormat.format(date)
        }
    }
}
