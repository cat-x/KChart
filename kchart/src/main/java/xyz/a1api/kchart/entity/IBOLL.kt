package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.CONFIG_TAG
import xyz.a1api.tools.LStorage

/**
 * 布林线指标接口
 * @see [](https://baike.baidu.com/item/%E5%B8%83%E6%9E%97%E7%BA%BF%E6%8C%87%E6%A0%87/3325894)相关说明
 * Created by tifezh on 2016/6/10.
 * For KChart
 * Cat-x All Rights Reserved
 */

interface IBOLL {

    /**
     * 上轨线
     */
    fun getUp(): Float

    /**
     * 中轨线
     */
    fun getMb(): Float

    /**
     * 下轨线
     */
    fun getDn(): Float

    class BOLL {
        var mb: Float = 0f
        var up: Float = 0f
        var dn: Float = 0f
    }


    object IBOLLConfig {
        val defaultBOLLLists: Pair<Int, Int> = Pair(20, 2)
        var useBOLLConfig: Pair<Int, Int>
            get() {
                val string = LStorage(CONFIG_TAG).getString(ICandleBoll.IMainConfig.typeBOLL)
                if (string.isNotBlank()) {
                    try {
                        val list = string.split("|").map { it.toInt() }
                        if (list.size == 2) {
                            return list[0] to list[1]
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return defaultBOLLLists
            }
            set(value) {
                LStorage(CONFIG_TAG).putString(
                    ICandleBoll.IMainConfig.typeBOLL,
                    "${value.first}|${value.second}"
                )
            }

    }
}
