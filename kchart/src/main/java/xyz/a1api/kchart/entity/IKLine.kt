package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.CONFIG_TAG
import xyz.a1api.tools.LStorage

/**
 * k线实体接口
 * Created by tifezh on 2016/6/9.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IKLine : ICandleBoll, IMACD, IKDJ, IRSI, IVolume, IWR {

    object ISecondDrawConfig {
        const val typeMACD = "MACD"
        const val typeKDJ = "KDJ"
        const val typeRSI = "RSI"
        const val typeWR = "WR"

        const val typeMCV = "MCV"

        var isShowSecondDrawIndicator: Boolean
            get() {
                return LStorage(CONFIG_TAG).getBoolean("isShowSecondDrawIndicator", true)
            }
            set(value) {
                LStorage(CONFIG_TAG).putBoolean("isShowSecondDrawIndicator", value)
            }

        var useSecondDrawIndicator: String
            get() {
                return LStorage(CONFIG_TAG).getString("useSecondDrawIndicator", typeMACD)
            }
            set(value) {
                LStorage(CONFIG_TAG).putString("useSecondDrawIndicator", value)
            }
    }
}


