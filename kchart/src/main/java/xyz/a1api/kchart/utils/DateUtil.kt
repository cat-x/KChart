package xyz.a1api.kchart.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 * Created by tifezh on 2016/4/27.
 * For KChart
 * Cat-x All Rights Reserved
 */
object DateUtil {
    var dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    var dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
}
