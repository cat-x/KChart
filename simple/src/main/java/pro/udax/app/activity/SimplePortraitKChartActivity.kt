package pro.udax.app.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.stomped.stomped.client.StompedClient
import com.stomped.stomped.component.StompedFrame
import com.stomped.stomped.listener.StompedListener
import kotlinx.android.synthetic.main.activity_simple_kchart_portrait.*
import pro.udax.app.R
import pro.udax.app.network.entry.request.QuotationData
import xyz.a1api.kchart.simple.TimeResolution
import xyz.a1api.kchart.utils.startActivity


/**
 * Created by Cat-x on 2019/10/31.
 * For KChart
 * Cat-x All Rights Reserved
 */
class SimplePortraitKChartActivity : AppCompatActivity() {

    lateinit var mStompedClient: StompedClient

    var kChartView: SimpleKChartView? = null

    var mTimeResolution: TimeResolution
        get() {
            return kChartView?.mTimeResolution ?: TimeResolution.Invalid
        }
        set(value) {
            kChartView?.mTimeResolution = value
        }

//    val mAdapter: KChartAdapter<BaseKLineEntity>
//        get() {
//            return kChartView?.mAdapter ?: KChartAdapter()
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#131F30")
        }
        setContentView(R.layout.activity_simple_kchart_portrait)
        fullScreenView.setOnClickListener { startActivity<SimpleLandscapeKChartActivity>() }
        exitMarketView.setOnClickListener { onBackPressed() }
        kChartView = findViewById(R.id.simpleKChartView)
//        findViewById<KScrollView>(R.id.kScrollView)?.scaleView = kChartView?.mKChartView
        mTimeResolution = TimeResolution("BTC/GOLDT", 15)
        kChartView?.startLoad()
        startWebSocket()
    }


    fun startWebSocket() {
        mStompedClient = StompedClient.StompedClientBuilder().setHeartBeat(10000)
            .build("服务器的WebSocketURL")

        mStompedClient.subscribe("xxxx", object : StompedListener() {

            override fun onNotify(frame: StompedFrame) {
                Log.i("onNotify", frame.stompedBody)
                val string = frame.stompedBody
                val data =
                    Gson().fromJson<QuotationData.KLineData.QuotaKlineBean>(
                        string,
                        QuotationData.KLineData.QuotaKlineBean::class.java
                    )
                if (data.symbol == mTimeResolution.symbol && data.period == mTimeResolution.period) {
                    if (kChartView!!.getOriginalData().isNotEmpty()) {
                        val lastData = kChartView!!.getOriginalData().last()
                        val timeArray1 = lastData.getDateTime<String>().split(":")
                        var lastTime = timeArray1[0].toInt() * 60 + timeArray1[1].toInt()

                        val timeArray2 = data.getDateTime<String>().split(":")
                        var dataTime = timeArray2[0].toInt() * 60 + timeArray2[1].toInt()
                        runOnUiThread {
                            if (dataTime - lastTime > mTimeResolution.period) {
                                kChartView?.addRealTimeData(data, isNeedAddToData = true)
                            } else {
                                kChartView?.addRealTimeData(data)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mStompedClient.disconnect()
    }
}