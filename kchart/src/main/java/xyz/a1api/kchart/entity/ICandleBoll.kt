package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.CONFIG_TAG
import xyz.a1api.tools.LStorage

/**
 * Created by Cat-x on 2018/12/11.
 * For KChart
 * Cat-x All Rights Reserved
 */
interface ICandleBoll : ICandle, IBOLL {

    object IMainConfig {

        const val typeMA = "MA"
        const val typeBOLL = "BOLL"

        var isShowMainDrawIndicator: Boolean
            get() {
                return LStorage(CONFIG_TAG).getBoolean("isShowMainDrawIndicator", true)
            }
            set(value) {
                LStorage(CONFIG_TAG).putBoolean("isShowMainDrawIndicator", value)
            }

        var useMainDrawIndicator: String
            get() {
                return LStorage(CONFIG_TAG).getString("useMainDrawIndicator", typeMA)
            }
            set(value) {
                LStorage(CONFIG_TAG).putString("useMainDrawIndicator", value)
            }
    }
}