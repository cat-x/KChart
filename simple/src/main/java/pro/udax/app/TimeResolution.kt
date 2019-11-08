package xyz.a1api.kchart.simple

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import pro.udax.app.network.entry.request.QuotationQueryReq
import java.lang.Long.parseLong
import java.util.*

/**
 * Created by Cat-x on 2018/11/29.
 * For KChart
 * Cat-x All Rights Reserved
 */

@Parcelize
class TimeResolution(
    val symbol: String,
    val period: Long,
    var endTime: Long = Date().time / 1000,
    var startTime: Long = endTime
) : Parcelable {

    fun getQuotationQuery(isNext: Boolean = true): QuotationQueryReq {
        if (isNext) {
            if (endTime != startTime) {
                endTime = startTime
            }
            startTime = endTime - periodLengthSeconds(period.toString())
        }
        return QuotationQueryReq(symbol, period, startTime, endTime)
    }


    companion object {
        fun periodLengthSeconds(resolution: String, requiredPeriodsCount: Float = 200f): Long {
            var daysCount = 0f;
            if (resolution == "D" || resolution == "1D") {
                daysCount = requiredPeriodsCount;
            } else if (resolution == "M" || resolution == "1M") {
                daysCount = 31 * requiredPeriodsCount;
            } else if (resolution == "W" || resolution == "1W") {
                daysCount = 7 * requiredPeriodsCount;
            } else {
                daysCount = requiredPeriodsCount * parseLong(resolution) / (24 * 60);
            }
            return (daysCount * 24 * 60 * 60).toLong();
        }

        val Invalid = TimeResolution("", 0, 0, 0)
    }
}