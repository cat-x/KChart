package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.*
import xyz.a1api.tools.LStorage

/**
 * 蜡烛图实体接口
 * Created by tifezh on 2016/6/9.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface ICandle {

    /**
     * 开盘价
     */
    fun getOpenPrice(): Float

    /**
     * 最高价
     */
    fun getHighPrice(): Float

    /**
     * 最低价
     */
    fun getLowPrice(): Float

    /**
     * 收盘价
     */
    fun getClosePrice(): Float

    /**
     * 移动平均线（Moving Average，简称MA），它是将某一段时间的收盘价之和除以该周期。 比如日线MA5指5天内的收盘价除以5 。
     * num(月，日，时，分，5分等)均价
     * @param num Int
     * @return Float
     */
    fun getMAbyNum(num: Int): Float

    /**
     * maxNum(月，日，时，分，5分等)均价
     * @param num Int
     * @return Float
     */
    fun getMAbyMaxNum(): Float

    /**
     * maxNum(月，日，时，分，5分等)均价
     * @param num Int
     * @return Float
     */
    fun getMAbyMinNum(): Float

    object ICandleConfig {
        val defaultMALists: HashMap<Int, EnableItem> =
            hashMapOf(0 to EnableItem(5), 1 to EnableItem(10), 2 to EnableItem(30))
        var customizeMALists: HashMap<Int, EnableItem>
            get() {
                return LStorage(CONFIG_TAG).getString(ICandleBoll.IMainConfig.typeMA).toShowConfig()
                    ?: defaultMALists
            }
            set(value) {
                LStorage(CONFIG_TAG).putString(ICandleBoll.IMainConfig.typeMA, value.toSaveString())
            }

        fun getCalculateMAConfig(): IntArray {
            return customizeMALists.toCalculate()
        }

    }


}
