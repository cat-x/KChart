package pro.udax.app.activity

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import pro.udax.app.EasyFormatter
import pro.udax.app.network.RetrofitTools
import pro.udax.app.network.done
import xyz.a1api.kchart.customize.BaseKLineEntity
import xyz.a1api.kchart.simple.TimeResolution
import xyz.a1api.kchart.widget.ConfigKChartView

/**
 * Created by Cat-x on 2019/10/31.
 * For KChart
 * Cat-x All Rights Reserved
 */
class SimpleKChartView : ConfigKChartView {

    lateinit var mTimeResolution: TimeResolution

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setDateTimeFormatter(EasyFormatter())
    }

    override fun onRefreshData(
        onLoadSuccess: (data: List<BaseKLineEntity>, isHasNext: Boolean) -> Unit,
        onLoadFail: (tip: () -> Unit) -> Unit
    ) {
        RetrofitTools.apiService.hisQuotation(mTimeResolution.getQuotationQuery())
            .done({ data, body ->
                if (data?.code == 200) {
                    onLoadSuccess(data.data!!.kLineData.kLineList, data.data.kLineData.hasNext)
                }
            }, {
                onLoadFail {
                    if (it.message == "response.body() is null") {
//                        context.toast("网络开小差啦，请重试. 错误码:" + it.cause)
                    } else {
//                        context.toast("网络开小差啦，请重试")
                    }
                }
            }, 2)
    }


    override fun onSwitchTime(oldTime: Long, newTime: Long) {
        mTimeResolution = TimeResolution(mTimeResolution.symbol, newTime)
    }
}