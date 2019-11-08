package xyz.a1api.kchart.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import xyz.a1api.kchart.R
import xyz.a1api.kchart.base.IDateTimeFormatter
import xyz.a1api.kchart.customize.BaseKLineEntity
import xyz.a1api.kchart.customize.KChartAdapter
import xyz.a1api.kchart.customize.KChartIndicatorConfigActivity
import xyz.a1api.kchart.entity.ICandleBoll
import xyz.a1api.kchart.entity.IKLine
import xyz.a1api.kchart.entity.IKMLine
import xyz.a1api.kchart.formatter.QuaFormatter
import xyz.a1api.kchart.utils.*
import xyz.a1api.kchart.view.BaseKChartView
import xyz.a1api.kchart.view.KChartView
import java.lang.ref.SoftReference


/**
 * Created by Cat-x on 2019/10/30.
 * For KChart
 * Cat-x All Rights Reserved
 */
abstract class ConfigKChartView : RelativeLayout {

    var mAdapter: KChartAdapter<BaseKLineEntity>? = null
    lateinit var mKChartView: KChartView
    protected var lastSelectView: TimeStepView? = null
    protected var isLandSpace = false

    constructor(context: Context?) : super(context) {
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(attrs)
    }


    protected fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ConfigKChartView)
            if (array != null) {
                isLandSpace = array.getBoolean(R.styleable.ConfigKChartView_isLandSpace, false)
                array.recycle()
            }
        }

        val view =
            if (isLandSpace) View.inflate(context, R.layout.kchart_config_view_landscape, this)
            else View.inflate(context, R.layout.kchart_config_view_portrait, this)


        mKChartView = view.findViewById(R.id.mKChartView)
        setTimeViews()
        initMainChart(
            view.findViewById(R.id.mainChartMAView),
            view.findViewById(R.id.mainChartBOLLView),
            view.findViewById(R.id.mainChartSeeView)
        )
        initSecondChart(
            view.findViewById(R.id.secondChartMACDView),
            view.findViewById(R.id.secondChartKDJView),
            view.findViewById(R.id.secondChartRSIView),
            view.findViewById(R.id.secondChartWRView),
            view.findViewById(R.id.secondChartSeeView)
        )
        if (isLandSpace) {
            findViewById<View>(R.id.configChartDataView).setOnClickListener {
                KChartIndicatorConfigActivity.mConfigKChartView = SoftReference(this)
                context.startActivity<KChartIndicatorConfigActivity>()
            }//指标设置
            mKChartView.setGridRows(4)
            mKChartView.setGridColumns(5)
        } else {
            findViewById<View>(R.id.configChartDataView).setOnClickListener {
                KChartIndicatorConfigActivity.mConfigKChartView = SoftReference(this)
                context.startActivity<KChartIndicatorConfigActivity>()
            }//指标设置
            findViewById<View>(R.id.timeStepMoreView).setOnClickListener { findViewById<View>(R.id.tradeMoreTimeGroupLinearLayout).setNegateVisibility() }//更多时间选项
            findViewById<View>(R.id.moreTradeToolView).setOnClickListener { findViewById<View>(R.id.tradeDataIndicatorLinearLayout).setNegateVisibility() }//数据指标器选项
            mKChartView.setGridRows(5)
            mKChartView.setGridColumns(5)
        }

        initKChartData(mKChartView)
    }


    open fun initKChartData(kChartView: KChartView) {
        this.mKChartView = kChartView
        mAdapter = KChartAdapter(kChartView)
        kChartView.setAdapter(mAdapter!!)
        kChartView.setDateTimeFormatter(QuaFormatter())
        if (isDebug) {
            kChartView.setOnSelectedChangedListener(object :
                BaseKChartView.OnSelectedChangedListener {
                override fun onSelectedChanged(view: BaseKChartView, point: Any, index: Int) {
                    val data = point as BaseKLineEntity
                    Log.i(
                        "onSelectedChanged",
                        "index:" + index + " openPrice:" + data.getOpenPrice() + " closePrice:" + data.getClosePrice()
                    )
                }
            })
        }

        kChartView.setRefreshListener(object : KChartView.KChartRefreshListener {
            override fun onLoadMoreBegin(chart: KChartView) {
                try {
                    doAsync {
                        onRefreshData(::onLoadSuccess, ::onLoadFail)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    open fun startLoad() {
        mKChartView.showLoading(true, false)
    }

    /**
     * 当需要刷新数据时（历史数据，向前滑动）
     */
    abstract fun onRefreshData(
        onLoadSuccess: (data: List<BaseKLineEntity>, isHasNext: Boolean) -> Unit,
        onLoadFail: (tip: () -> Unit) -> Unit = {}
    )

    /**
     * 数据加载成功（历史数据，向前滑动）
     */
    open fun onLoadSuccess(data: List<BaseKLineEntity>, isHasNext: Boolean) {
        context.runOnUiThread {
            mKChartView.mJob?.cancel()
            //第一次加载时开始动画
            if (mAdapter!!.getCount() == 0) {
                mKChartView.startAnimation()
            }
            if (data.isNotEmpty()) {
                mAdapter!!.addPrevData(data)
            }
            mKChartView.refreshComplete()

            if (!isHasNext) {
                mKChartView.refreshEnd()
            }
        }
    }

    /**
     * 数据加载失败（历史数据，向前滑动）
     */
    open fun onLoadFail(tip: () -> Unit) {
        context.runOnUiThread {
            mKChartView.mJob?.cancel()
            mKChartView.refreshComplete()
            tip()
        }
    }

    open fun initMainChart(
        tMAView: IndicatorTextView,
        tBOLLView: IndicatorTextView,
        showMainView: ImageView
    ) {
        tMAView.setClickMainIndicator(mKChartView) {
            tBOLLView.isSelected = false
            showMainView.isSelected = true
        }
        tBOLLView.setClickMainIndicator(mKChartView) {
            tMAView.isSelected = false
            showMainView.isSelected = true
        }
        showMainView.setOnClickListener {
            showMainView.isSelected = !showMainView.isSelected
            ICandleBoll.IMainConfig.isShowMainDrawIndicator = showMainView.isSelected
            if (showMainView.isSelected) {
                tMAView.isSelected = DrawIndicator.isMain(ICandleBoll.IMainConfig.typeMA)
                tBOLLView.isSelected = DrawIndicator.isMain(ICandleBoll.IMainConfig.typeBOLL)
                mKChartView.mainDrawIndicator = ICandleBoll.IMainConfig.useMainDrawIndicator
            } else {
                tMAView.isSelected = false
                tBOLLView.isSelected = false
                mKChartView.mainDrawIndicator = ""
            }
        }
        when {
            ICandleBoll.IMainConfig.isShowMainDrawIndicator -> {
                showMainView.isSelected = true
                when {
                    ICandleBoll.IMainConfig.useMainDrawIndicator ==
                            ICandleBoll.IMainConfig.typeMA -> tMAView.isSelected = true
                    ICandleBoll.IMainConfig.useMainDrawIndicator ==
                            ICandleBoll.IMainConfig.typeBOLL -> tBOLLView.isSelected = true
                }
            }
            else -> {
                mKChartView.mainDrawIndicator = ""
                showMainView.isSelected = false
            }
        }
    }

    open fun initSecondChart(
        tMACDView: IndicatorTextView,
        tKDJView: IndicatorTextView,
        tRSIView: IndicatorTextView,
        tWRView: IndicatorTextView,
        showSecondView: ImageView
    ) {
        tMACDView.setClickSecondIndicator(mKChartView) {
            tKDJView.isSelected = false
            tRSIView.isSelected = false
            tWRView.isSelected = false
            showSecondView.isSelected = true
        }
        tKDJView.setClickSecondIndicator(mKChartView) {
            tMACDView.isSelected = false
            tRSIView.isSelected = false
            tWRView.isSelected = false

            showSecondView.isSelected = true

        }
        tRSIView.setClickSecondIndicator(mKChartView) {
            tMACDView.isSelected = false
            tKDJView.isSelected = false
            tWRView.isSelected = false

            showSecondView.isSelected = true
        }
        tWRView.setClickSecondIndicator(mKChartView) {
            tMACDView.isSelected = false
            tKDJView.isSelected = false
            tRSIView.isSelected = false

            showSecondView.isSelected = true
        }
        showSecondView.setOnClickListener {
            showSecondView.isSelected = !showSecondView.isSelected
            IKLine.ISecondDrawConfig.isShowSecondDrawIndicator = showSecondView.isSelected
            if (showSecondView.isSelected) {
                tMACDView.isSelected = DrawIndicator.isSecond(IKLine.ISecondDrawConfig.typeMACD)
                tKDJView.isSelected = DrawIndicator.isSecond(IKLine.ISecondDrawConfig.typeKDJ)
                tRSIView.isSelected = DrawIndicator.isSecond(IKLine.ISecondDrawConfig.typeRSI)
                tWRView.isSelected = DrawIndicator.isSecond(IKLine.ISecondDrawConfig.typeWR)
                mKChartView.secondDrawIndicator = IKLine.ISecondDrawConfig.useSecondDrawIndicator
            } else {
                tMACDView.isSelected = false
                tKDJView.isSelected = false
                tRSIView.isSelected = false
                tWRView.isSelected = false
                mKChartView.secondDrawIndicator = ""
            }
        }

        when {
            IKLine.ISecondDrawConfig.isShowSecondDrawIndicator -> {
                mKChartView.secondDrawIndicator = IKLine.ISecondDrawConfig.useSecondDrawIndicator
                showSecondView.isSelected = true
                when {
                    IKLine.ISecondDrawConfig.useSecondDrawIndicator ==
                            IKLine.ISecondDrawConfig.typeMACD -> tMACDView.isSelected = true
                    IKLine.ISecondDrawConfig.useSecondDrawIndicator ==
                            IKLine.ISecondDrawConfig.typeKDJ -> tKDJView.isSelected = true
                    IKLine.ISecondDrawConfig.useSecondDrawIndicator ==
                            IKLine.ISecondDrawConfig.typeRSI -> tRSIView.isSelected = true
                    IKLine.ISecondDrawConfig.useSecondDrawIndicator ==
                            IKLine.ISecondDrawConfig.typeWR -> tWRView.isSelected = true
                }
            }
            else -> {
                mKChartView.secondDrawIndicator = ""
                showSecondView.isSelected = false
            }


        }

    }


    open fun initTimeSelect(vararg timesViews: Pair<TimeStepView, Long>) {
        for (timesView in timesViews) {

            if (IKMLine.ITimeStepConfig.useMainOrMin) {
                if (!timesView.first.isTimeMinute() && timesView.second == IKMLine.ITimeStepConfig.timeStep) {
                    lastSelectView = timesView.first
                    lastSelectView?.isSelected = true

                }
            } else {
                if (timesView.first.isTimeMinute()) {
                    lastSelectView = timesView.first
                    lastSelectView?.isSelected = true
                }
            }

            timesView.first.setOnClickListener {
                if (lastSelectView != timesView.first) {
                    lastSelectView?.isSelected = false
                    if (timesView.first.isTimeMinute()) {
                        mKChartView.useMainOrMin(false)
                    } else {
                        mKChartView.useMainOrMin(true)
                    }
                    timesView.first.isSelected = true
                    lastSelectView = timesView.first
                    IKMLine.ITimeStepConfig.timeStep = timesView.second

                    if (oldTime != timesView.second) {
                        onSwitchTime(oldTime, timesView.second)
                        reRequest()
                        oldTime = timesView.second
                    }
                }
            }
        }
    }

    open fun setTimeViews() {
        val timeViews: ArrayList<Pair<TimeStepView, Long>> = arrayListOf()
        timeViews.add(Pair(findViewById(R.id.timeStepTimeSharingView), 1))
        timeViews.add(Pair(findViewById(R.id.timeStep1MinView), 1))
        timeViews.add(Pair(findViewById(R.id.timeStep5MinView), 5))
        timeViews.add(Pair(findViewById(R.id.timeStep15MinView), 15))
        timeViews.add(Pair(findViewById(R.id.timeStep30MinView), 30))
        timeViews.add(Pair(findViewById(R.id.timeStep1HourView), 60))
        timeViews.add(Pair(findViewById(R.id.timeStep4HourView), 4 * 60))
        timeViews.add(Pair(findViewById(R.id.timeStep1DayView), 24 * 60))
        timeViews.add(Pair(findViewById(R.id.timeStep1WeekHourView), 7 * 24 * 60))
        timeViews.add(Pair(findViewById(R.id.timeStep1MonthView), 30 * 24 * 60))
        initTimeSelect(*timeViews.toTypedArray())
    }

    var oldTime: Long = 0
    abstract fun onSwitchTime(oldTime: Long, newTime: Long)

    open fun reRequest() {
        mAdapter = KChartAdapter()
        mAdapter!!.mKChartView = mKChartView
        mKChartView.setAdapter(mAdapter!!)
        mKChartView.refreshComplete()
        mKChartView.resetLoadMoreEnd()
        mKChartView.showLoading()
    }

    /**
     * 添加实时数据，用于最后一列数据跳动变化显示
     * @param data 数据
     * @param isNeedAddToData 是否永久添加到数据集合列表
     */
    @Suppress("unused")
    open fun addRealTimeData(data: BaseKLineEntity, isNeedAddToData: Boolean = false) {
        mAdapter?.addRealTimeData(data, isNeedAddToData)
    }

    /**
     * 获取原始的数据集合的实体 （不会包含实时数据）
     * @return ArrayList<T>
     */
    open fun getOriginalData(): ArrayList<BaseKLineEntity> {
        return mAdapter?.getOriginalData() ?: arrayListOf()
    }

    /**
     * 获取数据集合的实体 （存在实时数据时，会包含实时数据）
     */

    open fun getData(): List<BaseKLineEntity> {
        return mAdapter?.getData() ?: listOf()
    }

//    open fun getColorByStates(isSelect: Boolean = false): Int {
//        return if (isSelect) selectColor else unSelectColor
//    }
//
//    open val selectColor: Int = R.color.black_select_chart
//
//    open val unSelectColor: Int = R.color.black_noSelect_chart

    open fun setDateTimeFormatter(dateTimeFormatter: IDateTimeFormatter) {
        mKChartView.setDateTimeFormatter(dateTimeFormatter)
    }
}
