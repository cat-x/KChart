package xyz.a1api.kchart.customize

import xyz.a1api.kchart.entity.*

/**
 * Created by Cat-x on 2018/11/30.
 * For KChart
 * Cat-x All Rights Reserved
 */
abstract class BaseKLineEntity : IKMLine {
    open var macd: IMACD.MACD = IMACD.MACD()
    open var kdj: IKDJ.KDJ = IKDJ.KDJ()
    open var boll: IBOLL.BOLL = IBOLL.BOLL()

    open var dataRSIMaps = hashMapOf<Int, Float>()
    open var dataWRMaps = hashMapOf<Int, Float>()
    open var dataVolumeMAMaps = hashMapOf<Int, Float>()
    open var dataMAMaps = hashMapOf<Int, Float>()

    override fun getUp() = boll.up

    override fun getMb() = boll.mb

    override fun getDn() = boll.dn


    override fun getDea() = macd.dea

    override fun getDif() = macd.dif

    override fun getMacd() = macd.macd


    override fun getK() = kdj.k

    override fun getD() = kdj.d

    override fun getJ() = kdj.j


    override fun getRsiByNum(num: Int): Float {
        val data = dataRSIMaps[num]
        require(data != null) { "${this} for Rsi $num  is null" }
        return data
    }

    override fun getRByNum(num: Int): Float {
        val data = dataWRMaps[num]
        require(data != null) { "${this} for WR $num  is null" }
        return data
    }

    override fun getMAVolumeByNum(num: Int): Float {
        val data = dataVolumeMAMaps[num]
        require(data != null) { "${this} for MAVolume $num  is null" }
        return data
    }

    override fun getMAbyNum(num: Int): Float {
        val data = dataMAMaps[num]
        require(data != null) { "${this} for MA $num  is null" }
        return data
    }

    override fun getMAbyMaxNum(): Float {
        val max = ICandle.ICandleConfig.getCalculateMAConfig().maxBy { getMAbyNum(it) }
        return if (max == null) 0f else getMAbyNum(max)
    }

    override fun getMAbyMinNum(): Float {
        val min = ICandle.ICandleConfig.getCalculateMAConfig().minBy { getMAbyNum(it) }
        return if (min == null) 0f else getMAbyNum(min)
    }

    /**
     * 用于展示的时间
     */
    abstract fun getShowDateTime(): String

    /**
     * 用于计算的时间
     */
    abstract fun <T> getDateTime(): T

}