package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.CONFIG_TAG
import xyz.a1api.tools.LStorage

/**
 * MACD指标(指数平滑移动平均线)接口
 * @see [](https://baike.baidu.com/item/MACD指标)相关说明
 * Created by tifezh on 2016/6/10.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IMACD {


    /**
     * DEA值
     */
    fun getDea(): Float

    /**
     * DIF值
     */
    fun getDif(): Float

    /**
     * MACD值
     */
    fun getMacd(): Float

    class MACD {
        /**
         * DEA值
         */
        var dea: Float = 0f

        /**
         * DIF值
         */
        var dif: Float = 0f

        /**
         * MACD值
         */
        var macd: Float = 0f
    }

    object IMACDConfig {
        val defaultMACDLists: MACD = MACD(12, 26, 9)
        var useMACDConfig: MACD
            get() {
                val string = LStorage(CONFIG_TAG).getString(IKLine.ISecondDrawConfig.typeMACD)
                if (string.isNotBlank()) {
                    try {
                        val list = string.split("|").map { it.toInt() }
                        if (list.size == 3) {
                            return MACD(list[0], list[1], list[2])
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return defaultMACDLists
            }
            set(value) {
                LStorage(CONFIG_TAG).putString(
                    IKLine.ISecondDrawConfig.typeMACD,
                    "${value.S}|${value.L}|${value.M}"
                )
            }

        data class MACD(var S: Int, var L: Int, var M: Int) {
            override fun toString(): String {
                return "($S, $L, $M)"
            }
        }

    }

}
