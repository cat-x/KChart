package pro.udax.app.network.entry.request

import com.google.gson.annotations.SerializedName
import xyz.a1api.kchart.customize.BaseKLineEntity

data class QuotationQueryReq(
    @SerializedName("symbol")
    val symbol: String, // BTC/GOLDT
    @SerializedName("step")
    val step: Long, // 15
    @SerializedName("startTimeStamp")
    val startTimeStamp: Long, // 1543418774
    @SerializedName("endTimeStamp")
    val endTimeStamp: Long // 1543434322
)

data class QuotationData(
    @SerializedName("kLineData")
    val kLineData: KLineData = KLineData()
) {
    data class KLineData(
        @SerializedName("hasNext")
        val hasNext: Boolean = true, // true
        @SerializedName("kLineList")
        val kLineList: List<QuotaKlineBean> = listOf()
    ) {
        data class QuotaKlineBean(
            @SerializedName("close")
            val close: Float = 0.0f, // 3927.38
            @SerializedName("date")
            val date: String = "", // 20181128
            @SerializedName("high")
            val high: Float = 0.0f, // 3931.4
            @SerializedName("low")
            val low: Float = 0.0f, // 3927.38
            @SerializedName("open")
            val `open`: Float = 0.0f, // 3931.4
            @SerializedName("period")
            val period: Long = 0, // 1
            @SerializedName("sTimeStamp")
            val sTimeStamp: Int = 0, // 1543364958
            @SerializedName("symbol")
            val symbol: String = "", // BTC/USDT
            @SerializedName("time")
            val time: String = "", // 09:29:18
            @SerializedName("uTimeStamp")
            val uTimeStamp: Int = 0, // 1543364990
            @SerializedName("volume")
            val volumes: Float = 0.0f // 0.10699999999999932
        ) : BaseKLineEntity() {

            override fun getOpenPrice() = `open`

            override fun getHighPrice() = high

            override fun getLowPrice() = low

            override fun getClosePrice() = close

            override fun getVolume() = volumes

            override fun getPrice(): Float = close

            override fun getShowDateTime(): String {
                return date + " " + time
            }

            override fun <T> getDateTime(): T {
                return time as T
            }
        }
    }
}
