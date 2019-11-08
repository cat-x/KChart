package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.*
import xyz.a1api.tools.LStorage

/**
 * @author fujianlian Created on 2018/8/20 15:16
 * @descripe WR指标(随机指标)接口
 */
interface IWR {

    /**
     * %R值
     */
    fun getRByNum(num: Int): Float

    object IWRConfig {
        val defaultWRLists: HashMap<Int, EnableItem> =
            hashMapOf(0 to EnableItem(7, false), 1 to EnableItem(14), 2 to EnableItem(20, false))
        var customizWRLists: HashMap<Int, EnableItem>
            get() {
                return LStorage(CONFIG_TAG).getString(IKLine.ISecondDrawConfig.typeWR).toShowConfig()
                    ?: defaultWRLists
            }
            set(value) {
                LStorage(CONFIG_TAG).putString(
                    IKLine.ISecondDrawConfig.typeWR,
                    value.toSaveString()
                )
            }

        fun getCalculateWRConfig(): IntArray {
            return customizWRLists.toCalculate()
        }

    }

}