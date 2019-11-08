package xyz.a1api.kchart.utils

import xyz.a1api.kchart.entity.ICandleBoll
import xyz.a1api.kchart.entity.IKLine

/**
 * Created by Cat-x on 2018/11/26.
 * For KChart
 * Cat-x All Rights Reserved
 */
//class KChartConfig {
//    var showSecondPicture: Boolean = true
//    var mainPictureIndicator: String = "MA"
//    var secondPictureIndicator: String = "MACD"
//    var configMAList: ArrayList<EnableItem> = arrayListOf()
//    var configRSIList: ArrayList<EnableItem> = arrayListOf()
//    var configMACD: ArrayList<Int> = arrayListOf()
//    var configBOLL: ArrayList<Int> = arrayListOf()
//    var configKDJ: ArrayList<Int> = arrayListOf()
//    var configMAVolume: ArrayList<EnableItem> = arrayListOf()
//}


const val CONFIG_TAG = "kChart"

var isDebug: Boolean = true

class EnableItem(
    var name: Int = 0,
    var enable: Boolean = true
) {

}

object DrawIndicator {

    fun isMain(name: String): Boolean {
        return ICandleBoll.IMainConfig.useMainDrawIndicator == name
    }

    fun isSecond(name: String): Boolean {
        return IKLine.ISecondDrawConfig.useSecondDrawIndicator == name
    }

}

