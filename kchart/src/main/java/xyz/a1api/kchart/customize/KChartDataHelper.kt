package xyz.a1api.kchart.customize


import xyz.a1api.kchart.entity.*
import java.math.RoundingMode

/**
 * 数据辅助类 计算macd rsi等
 */
@Suppress("MemberVisibilityCanBePrivate")
object KChartDataHelper {

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param datas
     */
    fun calculate(datas: List<BaseKLineEntity>) {
        calculateMA(datas, *ICandle.ICandleConfig.getCalculateMAConfig())
        calculateMACD(datas, IMACD.IMACDConfig.useMACDConfig)
        calculateBOLL(datas, IBOLL.IBOLLConfig.useBOLLConfig)
        calculateRSI(datas, *IRSI.IRSIConfig.getCalculateRSIConfig())
        calculateWR(datas, *IWR.IWRConfig.getCalculateWRConfig())
        calculateKDJ(datas, IKDJ.IKDJConfig.useKDJConfig)
        calculateVolumeMA(datas, *IVolume.IVolumeMAConfig.getVolumeMAConfig())
    }

    /**
     * 移动平均线（Moving Average，简称MA），它是将某一段时间的收盘价之和除以该周期。 比如日线MA5指5天内的收盘价除以5 。
     */
    fun calculateMA(datas: List<BaseKLineEntity>, vararg numbers: Int) {
        val ma = hashMapOf<Int, Float>()
        for (num in numbers) {
            ma[num] = 0f
        }
        for (i in datas.indices) {
            val point = datas[i]
            val closePrice = point.getClosePrice()
            for (num in numbers) {
                ma[num] = ma[num]!! + closePrice
                if (i >= num) {
                    ma[num] = ma[num]!! - datas[i - num].getClosePrice()
                    point.dataMAMaps[num] = ma[num]!! / num.toFloat()
                } else {
                    point.dataMAMaps[num] = ma[num]!! / (i + 1f)
                }
            }
        }
    }

    /**
     * MACD在应用上应先行计算出快速（一般选12日）移动平均值与慢速（一般选26日）移动平均值。以这两个数值作为测量两者（快速与慢速线）间的“差离值”依据。所谓“差离值”（DIF），即12日EMA数值减去26日EMA数值。
     * 计算macd
     *
     * @param datas
     */
    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    fun calculateMACD(datas: List<BaseKLineEntity>, config: IMACD.IMACDConfig.MACD) {
        val shortDay = config.S
        val longDay = config.L
        val diffDay = config.M
        var emaFast = 0f
        var emaSlow = 0f
        var dif = 0f
        var dea = 0f
        var macd = 0f

        for (i in datas.indices) {
            val point = datas[i]
            val closePrice = point.getClosePrice()
            if (i == 0) {
                emaFast = closePrice
                emaSlow = closePrice
            } else {
                //                EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                //                EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                emaFast =
                    emaFast * (shortDay - 1) / (shortDay + 1) + closePrice * 2f / (shortDay + 1)
                emaSlow = emaSlow * (longDay - 1) / (longDay + 1) + closePrice * 2f / (longDay + 1)
            }
            //            DIF = EMA（12） - EMA（26） 。
            //            今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            //            用（DIF-DEA）*2即为MACD柱状图。
            dif = emaFast - emaSlow
            dea = dea * (diffDay - 1) / (diffDay + 1) + dif * 2f / (diffDay + 1)
            macd = (dif - dea) * 2f
            point.macd.dif = dif
            point.macd.dea = dea
            point.macd.macd = macd
        }
    }

    /**
     * 计算 BOLL 需要在计算ma之后进行
     *
     * @param datas
     */
    fun calculateBOLL(datas: List<BaseKLineEntity>, bollConfig: Pair<Int, Int>) {
        val nValue = bollConfig.first
        val pValue = bollConfig.second.toFloat()
        //如果MA中没有指定的n值，则计算
        if (!ICandle.ICandleConfig.getCalculateMAConfig().contains(nValue)) {
            calculateMA(datas, nValue)
        }
        for (i in datas.indices) {
            val point = datas[i]
            val closePrice = point.getClosePrice()
            if (i == 0) {
                point.boll.mb = closePrice
                point.boll.up = java.lang.Float.NaN
                point.boll.dn = java.lang.Float.NaN
            } else {
                var n = nValue
                if (i < nValue) {
                    n = i + 1
                }
                var md = 0f
                for (j in i - n + 1..i) {
                    val c = datas[j].getClosePrice()
                    val m = point.getMAbyNum(nValue)
                    val value = c - m
                    md += value * value
                }
                md = md / (n - 1)
                md = Math.sqrt(md.toDouble()).toFloat()
                point.boll.mb = point.getMAbyNum(nValue)
                point.boll.up = point.boll.mb + pValue * md
                point.boll.dn = point.boll.mb - pValue * md
            }
        }
    }


    /**
     * RSI:= SMA(MAX(Close-LastClose,0),N,1)/SMA(ABS(Close-LastClose),N,1)*100
     * 计算RSI
     *
     * @param datas
     */
    fun calculateRSI(datas: List<BaseKLineEntity>, vararg numbers: Int) {
        val rsi = hashMapOf<Int, Float>()
        val rsiABSEma = hashMapOf<Int, Float>()
        val rsiMaxEma = hashMapOf<Int, Float>()
        for (num in numbers) {
            rsi[num] = 0f
            rsiABSEma[num] = 0f
            rsiMaxEma[num] = 0f
        }
        for (i in datas.indices) {
            val point = datas[i]
            val closePrice = point.getClosePrice()
            for (num in numbers) {
                if (i == 0) {
                    rsi[num] = 0f
                    rsiABSEma[num] = 0f
                    rsiMaxEma[num] = 0f
                } else {
                    val rMax = Math.max(0f, closePrice - datas[i - 1].getClosePrice())
                    val rAbs = Math.abs(closePrice - datas[i - 1].getClosePrice())
                    rsiMaxEma[num] = (rMax + (num - 1f) * rsiMaxEma[num]!!) / num
                    rsiABSEma[num] = (rAbs + (num - 1f) * rsiABSEma[num]!!) / num
                    rsi[num] = rsiMaxEma[num]!! / rsiABSEma[num]!! * 100f
                }
                point.dataRSIMaps[num] = rsi[num]!!
            }
        }
    }

    /**
     * 计算kdj
     * RSV:=(CLOSE-LLV(LOW,N))/(HHV(HIGH,N)-LLV(LOW,N))*100;
     * K:SMA(RSV,M1,1);
     * D:SMA(K,M2,1);
     * J:3*K-2*D;
     *
     * SMA(X,N,M)，求X的N日移动平均，M为权重。算法：若Y=SMA(X,N,M) 则 Y=(M*X+(N-M)*Y')/N，其中Y'表示上一周期Y值，N必须大于M。
     *
     * @param datas
     */
    fun calculateKDJ(datas: List<BaseKLineEntity>, config: Triple<Int, Int, Int>) {
        val nValue = config.first
        val m1Value = config.second.toFloat()
        val m2Value = config.third.toFloat()
        var k = 0f
        var d = 0f

        for (i in datas.indices) {
            val point = datas[i]
            val closePrice = point.getClosePrice()
            var startIndex = i - (nValue - 1)
            if (startIndex < 0) {
                startIndex = 0
            }
            var max9 = java.lang.Float.MIN_VALUE
            var min9 = java.lang.Float.MAX_VALUE
            for (index in startIndex..i) {
                max9 = Math.max(max9, datas[index].getHighPrice())
                min9 = Math.min(min9, datas[index].getLowPrice())
            }
            val rsv = 100f * (closePrice - min9) / (max9 - min9)
            if (i == 0) {
                k = rsv
                d = rsv
            } else {
                k = (rsv + (m1Value - 1) * k) / m1Value
                d = (k + (m2Value - 1) * d) / m2Value
            }
            point.kdj.k = k
            point.kdj.d = d
            point.kdj.j = 3f * k - 2 * d
        }
    }

    private fun calculateVolumeMA(entries: List<BaseKLineEntity>, vararg numbers: Int) {
        val volumeMa = hashMapOf<Int, Float>()
        for (num in numbers) {
            volumeMa[num] = 0f
        }

        for (i in entries.indices) {
            val entry = entries[i]
            for (num in numbers) {
                volumeMa[num] = volumeMa[num]!! + entry.getVolume()
                if (i >= num) {
                    volumeMa[num] = volumeMa[num]!! - entries[i - num].getVolume()
                    entry.dataVolumeMAMaps[num] = volumeMa[num]!! / num
                } else {
                    entry.dataVolumeMAMaps[num] = volumeMa[num]!! / (i + 1f)
                }
            }
        }
    }

    /**
     * 计算wr
     *
     *  WR=(Hn—C)÷(Hn—Ln)×100
     *  其中：C为计算日的收盘价，Ln为N周期内的最低价，Hn为N周期(包括当日)内的最高价，公式中的N为选定的计算时间参数，一般为4或14。
     *  以计算周期为14日为例，其计算过程如下：
     *  WR(14日)=(H14—C)÷(H14—L14)×100
     *
     * @param dataList
     */
    fun calculateWR(dataList: List<BaseKLineEntity>, vararg numbers: Int) {
        var r: Float?
        for (i in dataList.indices) {
            val point = dataList[i]

            val maxDayNum = numbers.associate { it to java.lang.Float.MIN_VALUE }.toMutableMap()
            val minDayNum = numbers.associate { it to java.lang.Float.MAX_VALUE }.toMutableMap()
            for (number in numbers) {
                val startIndex = (i - (number - 1)).let { if (it < 0) 0 else it }
                for (index in startIndex..i) {
                    maxDayNum[number] =
                        Math.max(maxDayNum[number]!!, dataList[index].getHighPrice())
                    minDayNum[number] = Math.min(minDayNum[number]!!, dataList[index].getLowPrice())
                }

                if (i < number - 1) {
                    point.dataWRMaps[number] = -10f
                } else {
                    r =
                        ((maxDayNum[number]!!.toBigDecimal() - dataList[i].getClosePrice().toBigDecimal()).divide(
                            maxDayNum[number]!!.toBigDecimal() - minDayNum[number]!!.toBigDecimal(),
                            6,
                            RoundingMode.HALF_UP
                        ) * 100.000000f.toBigDecimal()).toFloat()
                    if (r.isNaN()) {
                        point.dataWRMaps[number] = 0f
                    } else {
                        point.dataWRMaps[number] = r
                    }
                }
            }

        }

    }
}
