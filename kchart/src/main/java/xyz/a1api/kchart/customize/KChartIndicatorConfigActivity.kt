package xyz.a1api.kchart.customize


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.android.synthetic.main.kchart_activity_indicator_config.*
import xyz.a1api.kchart.R
import xyz.a1api.kchart.entity.*
import xyz.a1api.kchart.utils.*
import xyz.a1api.kchart.widget.ConfigKChartView
import java.lang.ref.SoftReference


/**
 * Created by Cat-x on 2018/12/13.
 * For KChart
 * Cat-x All Rights Reserved
 */
open class KChartIndicatorConfigActivity : AppCompatActivity() {
    var refreshCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kchart_activity_indicator_config)
        initView()
    }


    open fun initView() {
        kChartBackButton.setOnClickListener { onBackPressed() }
        layoutMAView.setOnClickListener { infoMARelativeLayout.setNegateVisibility(/*{ it.startAnimation(mShowAction) }, { it.startAnimation(mHiddenAction) }*/) }
        layoutBOLLView.setOnClickListener { infoBOLLRelativeLayout.setNegateVisibility() }
        layoutMACDView.setOnClickListener { infoMACDRelativeLayout.setNegateVisibility() }
        layoutKDJView.setOnClickListener { infoKDJRelativeLayout.setNegateVisibility() }
        layoutRSIView.setOnClickListener {
            infoRSIRelativeLayout.setNegateVisibility()
            if (infoRSIRelativeLayout.isVisible) {
                contentScrollView.postDelayed({
                    contentScrollView.smoothScrollBy(0, infoRSIRelativeLayout.height)
                }, 150)
            }

        }
        layoutWRView.setOnClickListener {
            infoWRRelativeLayout.setNegateVisibility()
            if (infoWRRelativeLayout.isVisible) {
                contentScrollView.postDelayed({
                    contentScrollView.smoothScrollBy(0, infoWRRelativeLayout.height)
                }, 150)
            }

        }


        initMA()
        initBOLL()
        initMACD()
        initKDJ()
        initRSI()
        initWR()

        refreshMA()
        refreshBOLL()
        refreshMACD()
        refreshKDJ()
        refreshRSI()
        refreshWR()

        initCheckMA()
        initCheckRSI()
        initCheckWR()



        ICandle.ICandleConfig.customizeMALists
    }

    private fun initCheckMA() {
        val checkList =
            listOf<AppCompatImageView>(ma1Check, ma2Check, ma3Check, ma4Check, ma5Check, ma6Check)
        checkList.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.isSelected =
                ICandle.ICandleConfig.customizeMALists.get(index)?.enable ?: false

            appCompatImageView.setOnClickListener {
                appCompatImageView.isSelected = !appCompatImageView.isSelected
                val temp = ICandle.ICandleConfig.customizeMALists
                temp.get(index)?.enable = appCompatImageView.isSelected
                ICandle.ICandleConfig.customizeMALists = temp
                refreshMA()
            }

        }
    }

    private fun initCheckRSI() {
        val checkList =
            listOf<AppCompatImageView>(rsi1Check, rsi2Check, rsi3Check)
        checkList.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.isSelected =
                IRSI.IRSIConfig.customizRSILists.get(index)?.enable ?: false

            appCompatImageView.setOnClickListener {
                appCompatImageView.isSelected = !appCompatImageView.isSelected
                val temp = IRSI.IRSIConfig.customizRSILists
                temp.get(index)?.enable = appCompatImageView.isSelected
                IRSI.IRSIConfig.customizRSILists = temp
                refreshRSI()
            }
        }
    }

    private fun initCheckWR() {
        val checkList =
            listOf<AppCompatImageView>(wr1Check, wr2Check, wr3Check)
        checkList.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.isSelected =
                IWR.IWRConfig.customizWRLists.get(index)?.enable ?: false

            appCompatImageView.setOnClickListener {
                appCompatImageView.isSelected = !appCompatImageView.isSelected
                val temp = IWR.IWRConfig.customizWRLists
                temp.get(index)?.enable = appCompatImageView.isSelected
                IWR.IWRConfig.customizWRLists = temp
                refreshWR()
            }
        }
    }

    private fun refreshBOLL() {
        refreshCount++
        bollTagLinearLayout.removeAllViews()
        bollTagLinearLayout.addView(
            getTagTextView().apply {
                text = "N" + IBOLL.IBOLLConfig.useBOLLConfig.first.toString()
            }, getTagTextViewLayoutParams()
        )

        bollTagLinearLayout.addView(
            getTagTextView().apply {
                text = "P" + IBOLL.IBOLLConfig.useBOLLConfig.second.toString()
            }, getTagTextViewLayoutParams()
        )
    }

    private fun initBOLL() {
        bollNTextView.setText(IBOLL.IBOLLConfig.useBOLLConfig.first.toString())
        bollPTextView.setText(IBOLL.IBOLLConfig.useBOLLConfig.second.toString())

        bollNTextView.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IBOLL.IBOLLConfig.useBOLLConfig =
                    IBOLL.IBOLLConfig.useBOLLConfig.copy(first = text.toString().toInt())
                refreshBOLL()
            }
        }
        bollPTextView.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IBOLL.IBOLLConfig.useBOLLConfig =
                    IBOLL.IBOLLConfig.useBOLLConfig.copy(second = text.toString().toInt())
                refreshBOLL()
            }
        }

    }

    open fun getTagTextView(): AppCompatTextView {
        return AppCompatTextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(getResColor(R.color.kchart_config_indicator_tag_text))
        }
    }

    open fun getTagTextViewLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
            .apply { setMargins(0, 0, dip(4), 0) }
    }

    private fun refreshMA() {
        refreshCount++
        maTagLinearLayout.removeAllViews()
        val valueArray = ICandle.ICandleConfig.customizeMALists
        for (index in 0 until 6) {
            val enableItem = valueArray.get(index)
            if (enableItem?.enable == true) {
                maTagLinearLayout.addView(
                    getTagTextView().apply {
                        text = "MA" + enableItem.name.toString()
                    }, getTagTextViewLayoutParams()
                )
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun initMA() {
        val listMaView = listOf<AppCompatEditText>(
            ma1TextView,
            ma2TextView,
            ma3TextView,
            ma4TextView,
            ma5TextView,
            ma6TextView
        )

        val valueArray = ICandle.ICandleConfig.customizeMALists

        listMaView.forEachIndexed { index, appCompatEditText ->
            val value = valueArray.get(index)
            if (value != null) {
                appCompatEditText.setText(value.name.toString())
            } else {
                appCompatEditText.setText("")
            }

            appCompatEditText.doAfterTextChanged { text ->
                if (!text.isNullOrBlank()) {
                    ICandle.ICandleConfig.customizeMALists =
                        ICandle.ICandleConfig.customizeMALists.apply {
                            val item = ICandle.ICandleConfig.customizeMALists.get(index)
                            val isOldEnable = item?.enable ?: true
                            if (item != null) {
                                try {
                                    remove(index)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            put(index, EnableItem(text.toString().toInt(), isOldEnable))
                        }
                } else {
                    ICandle.ICandleConfig.customizeMALists =
                        ICandle.ICandleConfig.customizeMALists.apply {
                            remove(index)
                        }
                }
                refreshMA()
            }
        }
    }

    private fun refreshMACD() {
        refreshCount++
        macdTagLinearLayout.removeAllViews()
        macdTagLinearLayout.addView(
            getTagTextView().apply {
                text = "S" + IMACD.IMACDConfig.useMACDConfig.S.toString()
            }, getTagTextViewLayoutParams()
        )

        macdTagLinearLayout.addView(
            getTagTextView().apply {
                text = "L" + IMACD.IMACDConfig.useMACDConfig.L.toString()
            }, getTagTextViewLayoutParams()
        )

        macdTagLinearLayout.addView(
            getTagTextView().apply {
                text = "M" + IMACD.IMACDConfig.useMACDConfig.M.toString()
            }, getTagTextViewLayoutParams()
        )
    }

    private fun initMACD() {
        macdSText.setText(IMACD.IMACDConfig.useMACDConfig.S.toString())
        macdLText.setText(IMACD.IMACDConfig.useMACDConfig.L.toString())
        macdMText.setText(IMACD.IMACDConfig.useMACDConfig.M.toString())

        macdSText.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IMACD.IMACDConfig.useMACDConfig =
                    IMACD.IMACDConfig.useMACDConfig.copy(S = text.toString().toInt())
                refreshMACD()
            }
        }

        macdLText.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IMACD.IMACDConfig.useMACDConfig =
                    IMACD.IMACDConfig.useMACDConfig.copy(L = text.toString().toInt())
                refreshMACD()
            }
        }


        macdMText.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IMACD.IMACDConfig.useMACDConfig =
                    IMACD.IMACDConfig.useMACDConfig.copy(M = text.toString().toInt())
                refreshMACD()
            }
        }
    }

    private fun refreshKDJ() {
        refreshCount++
        kdjTagLinearLayout.removeAllViews()
        kdjTagLinearLayout.addView(
            getTagTextView().apply {
                text = "N" + IKDJ.IKDJConfig.useKDJConfig.first.toString()
            }, getTagTextViewLayoutParams()
        )

        kdjTagLinearLayout.addView(
            getTagTextView().apply {
                text = "M1-" + IKDJ.IKDJConfig.useKDJConfig.second.toString()
            }, getTagTextViewLayoutParams()
        )

        kdjTagLinearLayout.addView(
            getTagTextView().apply {
                text = "M2-" + IKDJ.IKDJConfig.useKDJConfig.third.toString()
            }, getTagTextViewLayoutParams()
        )
    }

    private fun initKDJ() {
        kdjNTextView.setText(IKDJ.IKDJConfig.useKDJConfig.first.toString())
        kdjM1TextView.setText(IKDJ.IKDJConfig.useKDJConfig.second.toString())
        kdjM2TextView.setText(IKDJ.IKDJConfig.useKDJConfig.third.toString())

        kdjNTextView.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IKDJ.IKDJConfig.useKDJConfig =
                    IKDJ.IKDJConfig.useKDJConfig.copy(first = text.toString().toInt())
                refreshKDJ()
            }
        }

        kdjM1TextView.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IKDJ.IKDJConfig.useKDJConfig =
                    IKDJ.IKDJConfig.useKDJConfig.copy(second = text.toString().toInt())
                refreshKDJ()
            }
        }


        kdjM2TextView.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) {
                IKDJ.IKDJConfig.useKDJConfig =
                    IKDJ.IKDJConfig.useKDJConfig.copy(third = text.toString().toInt())
                refreshKDJ()
            }
        }
    }

    private fun refreshRSI() {
        refreshCount++
        rsiTagLinearLayout.removeAllViews()
        val valueArray = IRSI.IRSIConfig.customizRSILists
        for (index in 0 until 3) {
            val enableItem = valueArray.get(index)
            if (enableItem?.enable == true) {
                rsiTagLinearLayout.addView(
                    getTagTextView().apply {
                        text = "RSI${index + 1}-" + enableItem.name.toString()
                    }, getTagTextViewLayoutParams()
                )
            }
        }
    }

    private fun initRSI() {
        val listMaView = listOf<AppCompatEditText>(
            rsi1TextView,
            rsi2TextView,
            rsi3TextView
        )

        val valueArray = IRSI.IRSIConfig.customizRSILists
        listMaView.forEachIndexed { index, appCompatEditText ->
            val value = valueArray.get(index)
            if (value != null) {
                appCompatEditText.setText(value.name.toString())
            } else {
                appCompatEditText.setText("")
            }
            appCompatEditText.doAfterTextChanged { text ->
                if (!text.isNullOrBlank()) {
                    IRSI.IRSIConfig.customizRSILists =
                        IRSI.IRSIConfig.customizRSILists.apply {
                            val item = IRSI.IRSIConfig.customizRSILists.get(index)
                            val isOldEnable = item?.enable ?: true
                            if (item != null) {
                                try {
                                    remove(index)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            put(index, EnableItem(text.toString().toInt(), isOldEnable))
                        }
                } else {
                    IRSI.IRSIConfig.customizRSILists =
                        IRSI.IRSIConfig.customizRSILists.apply {
                            remove(index)
                        }
                }
                refreshRSI()
            }
        }
    }

    private fun refreshWR() {
        refreshCount++
        wrTagLinearLayout.removeAllViews()
        val valueArray = IWR.IWRConfig.customizWRLists
        for (index in 0 until 3) {
            val enableItem = valueArray.get(index)
            if (enableItem?.enable == true) {
                wrTagLinearLayout.addView(
                    getTagTextView().apply {
                        text = "WR${index + 1}-" + enableItem.name.toString()
                    }, getTagTextViewLayoutParams()
                )
            }
        }
    }

    private fun initWR() {
        val listMaView = listOf<AppCompatEditText>(
            wr1TextView,
            wr2TextView,
            wr3TextView
        )

        val valueArray = IWR.IWRConfig.customizWRLists
        listMaView.forEachIndexed { index, appCompatEditText ->
            val value = valueArray.get(index)
            if (value != null) {
                appCompatEditText.setText(value.name.toString())
            } else {
                appCompatEditText.setText("")
            }
            appCompatEditText.doAfterTextChanged { text ->
                if (!text.isNullOrBlank()) {
                    IWR.IWRConfig.customizWRLists =
                        IWR.IWRConfig.customizWRLists.apply {
                            val item = IWR.IWRConfig.customizWRLists.get(index)
                            val isOldEnable = item?.enable ?: true
                            if (item != null) {
                                try {
                                    remove(index)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            put(index, EnableItem(text.toString().toInt(), isOldEnable))
                        }
                } else {
                    IWR.IWRConfig.customizWRLists =
                        IWR.IWRConfig.customizWRLists.apply {
                            remove(index)
                        }
                }
                refreshWR()
            }
        }
    }

    override fun onBackPressed() {
        if (refreshCount > 6) {
            mConfigKChartView?.get()?.mAdapter?.refreshData(true)
        }
        super.onBackPressed()
    }

    companion object {
        var mConfigKChartView: SoftReference<ConfigKChartView>? = null
    }
}


