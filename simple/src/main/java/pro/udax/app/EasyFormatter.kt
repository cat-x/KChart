package pro.udax.app

import xyz.a1api.kchart.base.IDateTimeFormatter

/**
 * Created by Cat-x on 2018/11/30.
 * For KChart
 * Cat-x All Rights Reserved
 */
class EasyFormatter : IDateTimeFormatter {
    override fun format(date: String?): String {
        return date ?: ""
    }
}