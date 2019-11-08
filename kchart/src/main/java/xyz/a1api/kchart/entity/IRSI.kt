package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.*
import xyz.a1api.tools.LStorage

/**
 * RSI指标接口
 * @see [](https://baike.baidu.com/item/RSI%E6%8C%87%E6%A0%87)相关说明
 * Created by tifezh on 2016/6/10.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IRSI {

    /**
     * RSI1值
     */
    fun getRsiByNum(num: Int): Float


    object IRSIConfig {
        //        val defaultRSILists:IntArray = intArrayOf(7,14)
        val defaultRSILists: HashMap<Int, EnableItem> =
            hashMapOf(0 to EnableItem(7, false), 1 to EnableItem(14), 2 to EnableItem(20, false))

        var customizRSILists: HashMap<Int, EnableItem>
            get() {
                return LStorage(CONFIG_TAG).getString(IKLine.ISecondDrawConfig.typeRSI).toShowConfig()
                    ?: defaultRSILists
            }
            set(value) {
                LStorage(CONFIG_TAG).putString(
                    IKLine.ISecondDrawConfig.typeRSI,
                    value.toSaveString()
                )
            }

        fun getCalculateRSIConfig(): IntArray {
            return customizRSILists.toCalculate()
        }

    }
}
