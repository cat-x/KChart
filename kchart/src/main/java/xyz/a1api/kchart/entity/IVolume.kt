package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.EnableItem
import xyz.a1api.kchart.utils.toCalculate

/**
 * 成交量接口
 * Created by hjm on 2017/11/14 17:46.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IVolume {

    /**
     * 开盘价
     */
    fun getOpenPrice(): Float

    /**
     * 收盘价
     */
    fun getClosePrice(): Float

    /**
     * 成交量
     */
    fun getVolume(): Float

    /**
     * Num(月，日，时，分，5分等)均量
     */
    fun getMAVolumeByNum(num: Int): Float


    object IVolumeMAConfig {
        val defaultVolumeMALists: IntArray = intArrayOf(5, 10)
        var customizeVolumeMALists: ArrayList<EnableItem>? = null
        fun getVolumeMAConfig(): IntArray {
            return customizeVolumeMALists?.toCalculate() ?: defaultVolumeMALists
        }
    }
}
