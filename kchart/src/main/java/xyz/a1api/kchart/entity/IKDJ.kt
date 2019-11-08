package xyz.a1api.kchart.entity

import xyz.a1api.kchart.utils.CONFIG_TAG
import xyz.a1api.tools.LStorage

/**
 * KDJ指标(随机指标)接口
 * @see [](https://baike.baidu.com/item/KDJ%E6%8C%87%E6%A0%87/6328421?fr=aladdin&fromid=3423560&fromtitle=kdj)相关说明
 * Created by tifezh on 2016/6/10.
 * For KChart
 * Cat-x All Rights Reserved
 */
interface IKDJ {

    /**
     * K值
     */
    fun getK(): Float

    /**
     * D值
     */
    fun getD(): Float

    /**
     * J值
     */
    fun getJ(): Float

    class KDJ {
        var k: Float = 0f
        var d: Float = 0f
        var j: Float = 0f
    }

    object IKDJConfig {
        val defaultKDJLists: Triple<Int, Int, Int> = Triple(9, 3, 3)
        var useKDJConfig: Triple<Int, Int, Int>
            get() {
                val string = LStorage(CONFIG_TAG).getString(IKLine.ISecondDrawConfig.typeKDJ)
                if (string.isNotBlank()) {
                    try {
                        val list = string.split("|").map { it.toInt() }
                        if (list.size == 3) {
                            return Triple(list[0], list[1], list[2])
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return defaultKDJLists
            }
            set(value) {
                LStorage(CONFIG_TAG).putString(
                    IKLine.ISecondDrawConfig.typeKDJ,
                    "${value.first}|${value.second}|${value.third}"
                )
            }
    }

}
