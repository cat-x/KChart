package xyz.a1api.kchart.entity

/**
 * 分时图实体接口
 * Created by tifezh on 2017/7/19.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IMinuteLine {

    val isUseAvgPrice: Boolean
        get() = false

    /**
     * 获取均价(可选)
     */
    fun getAvgPrice(): Float = 0f

    /**
     * 获取成交价
     */
    fun getPrice(): Float

    /**
     * 成交量
     */
    fun getVolume(): Float
}
