package xyz.a1api.kchart.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import xyz.a1api.kchart.R
import xyz.a1api.kchart.draw.*
import xyz.a1api.kchart.entity.IKLine
import xyz.a1api.kchart.entity.IKMLine

/**
 * k线图
 * Created by tian on 2016/5/20.
 * For KChart
 * Cat-x All Rights Reserved
 */
open class KChartView : BaseKChartView {

    internal open var mProgressBar: ProgressBar? = null
    private var isLoadMoreEnd = false
    private var mLastScrollEnable: Boolean = false
    private var mLastScaleEnable: Boolean = false

    private var mRefreshListener: KChartRefreshListener? = null
    var mJob: Job? = null
        protected set

    private lateinit var mMACDDraw: MACDDraw
    private lateinit var mBOLLDraw: BOLLDraw
    private lateinit var mRSIDraw: RSIDraw
    private lateinit var mWRDraw: WRDraw
    private lateinit var mMainDraw: MainDraw
    private lateinit var mKDJDraw: KDJDraw
    private lateinit var mVolumeDraw: VolumeDraw
    private lateinit var mVolumeMinDraw: VolumeMinDraw
    private lateinit var mMinuteChartDraw: MinuteChartDraw

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
        initAttrs(attrs)
    }

    open fun initProgressBar() {
        mProgressBar = ProgressBar(context)
        val layoutParams = RelativeLayout.LayoutParams(dp2px(50f), dp2px(50f))
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        addView(mProgressBar, layoutParams)
        mProgressBar?.visibility = View.GONE
    }

    private fun initView() {
        initProgressBar()
        mVolumeDraw = VolumeDraw(mChildVolRect, this)
        mVolumeMinDraw = VolumeMinDraw(mChildVolRect, this)
        mMACDDraw = MACDDraw(getChildRect(), this)
        mKDJDraw = KDJDraw(getChildRect(), this)
        mRSIDraw = RSIDraw(getChildRect(), this)
        mWRDraw = WRDraw(getChildRect(), this)
        mBOLLDraw = BOLLDraw(getChildRect(), this)
        mMinuteChartDraw = MinuteChartDraw(mMainRect, this)
        mMainDraw = MainDraw(mMainRect, this)
//        addChildDraw("VOL", mVolumeDraw)
        addChildDraw(IKLine.ISecondDrawConfig.typeMACD, mMACDDraw)
        addChildDraw(IKLine.ISecondDrawConfig.typeKDJ, mKDJDraw)
        addChildDraw(IKLine.ISecondDrawConfig.typeRSI, mRSIDraw)
        addChildDraw(IKLine.ISecondDrawConfig.typeWR, mWRDraw)
//        addChildDraw("BOLL", mBOLLDraw)
        addChildDraw(IKLine.ISecondDrawConfig.typeMCV, mMinuteChartDraw)
//        setVolumeDraw(mVolumeDraw)
//        setMainDraw(mMainDraw)
        useMainOrMin(IKMLine.ITimeStepConfig.useMainOrMin, false)
    }

    @SuppressLint("Recycle")
    private fun initAttrs(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.KChartView)
        array?.also {
            try {
                //public
                setPointWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_point_width,
                        getDimension(R.dimen.chart_point_width)
                    )
                )
                setTextSize(
                    it.getDimension(
                        R.styleable.KChartView_kc_text_size,
                        getDimension(R.dimen.chart_text_size)
                    )
                )
                setTextColor(
                    it.getColor(
                        R.styleable.KChartView_kc_text_color,
                        getColor(R.color.chart_text)
                    )
                )
                setTimeTextSize(
                    it.getDimension(
                        R.styleable.KChartView_kc_text_size,
                        getDimension(R.dimen.chart_text_time_size)
                    )
                )
                setTimeTextColor(
                    it.getColor(
                        R.styleable.KChartView_kc_text_color,
                        getColor(R.color.chart_text)
                    )
                )
                setLineWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_line_width,
                        getDimension(R.dimen.chart_line_width)
                    )
                )
                setCandleTextBackground(
                    it.getColor(
                        R.styleable.KChartView_kc_background_color,
                        getColor(R.color.chart_background)
                    )
                )
                setBackgroundColor(
                    it.getColor(
                        R.styleable.KChartView_kc_background_color,
                        getColor(R.color.chart_background)
                    )
                )
                setSelectedLineColor(
                    it.getColor(
                        R.styleable.KChartView_kc_selected_line_color,
                        getColor(R.color.chart_text)
                    )
                )
                setSelectedLineWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_selected_line_width,
                        getDimension(R.dimen.chart_line_width)
                    )
                )
                setGridLineWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_grid_line_width,
                        getDimension(R.dimen.chart_grid_line_width)
                    )
                )
                setGridLineColor(
                    it.getColor(
                        R.styleable.KChartView_kc_grid_line_color,
                        getColor(R.color.chart_grid_line)
                    )
                )
                //macd
                setMACDWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_macd_width,
                        getDimension(R.dimen.chart_candle_width)
                    )
                )
                setDIFColor(
                    it.getColor(
                        R.styleable.KChartView_kc_dif_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setDEAColor(
                    it.getColor(
                        R.styleable.KChartView_kc_dea_color,
                        getColor(R.color.chart_ma2)
                    )
                )
                setMACDColor(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma3)
                    )
                )
                //kdj
                setKColor(
                    it.getColor(
                        R.styleable.KChartView_kc_dif_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setDColor(
                    it.getColor(
                        R.styleable.KChartView_kc_dea_color,
                        getColor(R.color.chart_ma2)
                    )
                )
                setJColor(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma3)
                    )
                )
                //rsi
                setRSI1Color(
                    it.getColor(
                        R.styleable.KChartView_kc_dif_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setRSI2Color(
                    it.getColor(
                        R.styleable.KChartView_kc_dea_color,
                        getColor(R.color.chart_ma2)
                    )
                )
                setRSI3Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma3)
                    )
                )
                //wr

                setWR1Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setWR2Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma2)
                    )
                )
                setWR3Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma3)
                    )
                )
                //boll
                setUpColor(
                    it.getColor(
                        R.styleable.KChartView_kc_dif_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setMbColor(
                    it.getColor(
                        R.styleable.KChartView_kc_dea_color,
                        getColor(R.color.chart_ma2)
                    )
                )
                setDnColor(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma3)
                    )
                )
                //main
                setVolMa5Color(
                    it.getColor(
                        R.styleable.KChartView_kc_dif_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setVolMa10Color(
                    it.getColor(
                        R.styleable.KChartView_kc_dea_color,
                        getColor(R.color.chart_ma2)
                    )
                )

                setMa1Color(
                    it.getColor(
                        R.styleable.KChartView_kc_dif_color,
                        getColor(R.color.chart_ma1)
                    )
                )
                setMa2Color(
                    it.getColor(
                        R.styleable.KChartView_kc_dea_color,
                        getColor(R.color.chart_ma2)
                    )
                )
                setMa3Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma3)
                    )
                )
                setMa4Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma4)
                    )
                )
                setMa5Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma5)
                    )
                )
                setMa6Color(
                    it.getColor(
                        R.styleable.KChartView_kc_macd_color,
                        getColor(R.color.chart_ma6)
                    )
                )

                setCandleWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_candle_width,
                        getDimension(R.dimen.chart_candle_width)
                    )
                )
                setCandleLineWidth(
                    it.getDimension(
                        R.styleable.KChartView_kc_candle_line_width,
                        getDimension(R.dimen.chart_candle_line_width)
                    )
                )
                setSelectorBackgroundColor(
                    it.getColor(
                        R.styleable.KChartView_kc_selector_background_color,
                        getColor(R.color.chart_selector)
                    )
                )
                setSelectorTextSize(
                    it.getDimension(
                        R.styleable.KChartView_kc_selector_text_size,
                        getDimension(R.dimen.chart_selector_text_size)
                    )
                )
                setCandleSolid(it.getBoolean(R.styleable.KChartView_kc_candle_solid, true))
                //tab

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.recycle()
            }
        }
    }

    private fun getDimension(@DimenRes resId: Int): Float {
        return resources.getDimension(resId)
    }

    private fun getColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }

    override fun onLeftSide() {
        showLoading(true, true)
    }

    override fun onRightSide() {}

    open fun showLoading(isLoadMore: Boolean = false, needDelay: Boolean = false) {
        if (!isLoadMoreEnd && !isRefreshing) {
            isRefreshing = true
            if (needDelay) {
                GlobalScope.launch(Dispatchers.Main) {
                    mJob?.cancel()
                    mJob = launch(Dispatchers.Main) {
                        delay(500)
                        mProgressBar?.visibility = View.VISIBLE
                    }
                }
            } else {
                mProgressBar?.visibility = View.VISIBLE
            }

            if (isLoadMore) {
                mRefreshListener?.onLoadMoreBegin(this)
            }
            mLastScaleEnable = isScaleEnable()
            mLastScrollEnable = isScrollEnable()
            super.setScrollEnable(false)
            super.setScaleEnable(false)
        }
    }

    private fun hideLoading() {
        mProgressBar?.visibility = View.GONE
        super.setScrollEnable(mLastScrollEnable)
        super.setScaleEnable(mLastScaleEnable)
    }

    /**
     * 刷新完成
     */
    open fun refreshComplete() {
        isRefreshing = false
        hideLoading()
    }

    /**
     * 刷新完成，没有数据
     */
    open fun refreshEnd() {
        isLoadMoreEnd = true
        isRefreshing = false
        hideLoading()
    }

    /**
     * 重置加载更多
     */
    open fun resetLoadMoreEnd() {
        isLoadMoreEnd = false
    }

    interface KChartRefreshListener {
        /**
         * 加载更多
         * @param chart
         */
        fun onLoadMoreBegin(chart: KChartView)
    }

    override fun setScaleEnable(scaleEnable: Boolean) {
        if (isRefreshing) {
            throw IllegalStateException("请勿在刷新状态设置属性")
        }
        super.setScaleEnable(scaleEnable)

    }

    override fun setScrollEnable(scrollEnable: Boolean) {
        if (isRefreshing) {
            throw IllegalStateException("请勿在刷新状态设置属性")
        }
        super.setScrollEnable(scrollEnable)
    }

    /**
     * 设置DIF颜色
     */
    open fun setDIFColor(color: Int) {
        mMACDDraw.setDIFColor(color)
    }

    /**
     * 设置DEA颜色
     */
    open fun setDEAColor(color: Int) {
        mMACDDraw.setDEAColor(color)
    }

    /**
     * 设置MACD颜色
     */
    open fun setMACDColor(color: Int) {
        mMACDDraw.setMACDColor(color)
    }

    /**
     * 设置MACD的宽度
     * @param MACDWidth
     */
    open fun setMACDWidth(MACDWidth: Float) {
        mMACDDraw.setMACDWidth(MACDWidth)
    }

    /**
     * 设置up颜色
     */
    open fun setUpColor(color: Int) {
        mBOLLDraw.setUpColor(color)
        mMainDraw.setUpColor(color)
    }

    /**
     * 设置mb颜色
     * @param color
     */
    open fun setMbColor(color: Int) {
        mBOLLDraw.setMbColor(color)
        mMainDraw.setMbColor(color)
    }

    /**
     * 设置dn颜色
     */
    open fun setDnColor(color: Int) {
        mBOLLDraw.setDnColor(color)
        mMainDraw.setDnColor(color)
    }

    /**
     * 设置K颜色
     */
    open fun setKColor(color: Int) {
        mKDJDraw.setKColor(color)
    }

    /**
     * 设置D颜色
     */
    open fun setDColor(color: Int) {
        mKDJDraw.setDColor(color)
    }

    /**
     * 设置J颜色
     */
    open fun setJColor(color: Int) {
        mKDJDraw.setJColor(color)
    }

    open fun setCandleTextBackground(color: Int) {
        mMainDraw.setTextBackgroundColor(color)
    }

    open fun setVolMa5Color(color: Int) {
        mVolumeDraw.setMa5Color(color)
    }

    open fun setVolMa10Color(color: Int) {
        mVolumeDraw.setMa10Color(color)
    }

    /**
     * 设置ma1颜色
     * @param color
     */
    open fun setMa1Color(color: Int) {
        mMainDraw.setMa1Color(color)
    }

    /**
     * 设置ma2颜色
     * @param color
     */
    open fun setMa2Color(color: Int) {
        mMainDraw.setMa2Color(color)
    }

    /**
     * 设置ma3颜色
     * @param color
     */
    open fun setMa3Color(color: Int) {
        mMainDraw.setMa3Color(color)
    }

    /**
     * 设置ma4颜色
     * @param color
     */
    open fun setMa4Color(color: Int) {
        mMainDraw.setMa4Color(color)
    }

    /**
     * 设置ma5颜色
     * @param color
     */
    open fun setMa5Color(color: Int) {
        mMainDraw.setMa5Color(color)
    }

    /**
     * 设置ma6颜色
     * @param color
     */
    open fun setMa6Color(color: Int) {
        mMainDraw.setMa6Color(color)
    }

    /**
     * 设置选择器文字大小
     * @param textSize
     */
    open fun setSelectorTextSize(textSize: Float) {
        mMainDraw.setSelectorTextSize(textSize)
    }

    /**
     * 设置选择器背景
     * @param color
     */
    open fun setSelectorBackgroundColor(color: Int) {
        mMainDraw.setSelectorBackgroundColor(color)
    }

    /**
     * 设置蜡烛宽度
     * @param candleWidth
     */
    open fun setCandleWidth(candleWidth: Float) {
        mMainDraw.setCandleWidth(candleWidth)
    }

    /**
     * 设置蜡烛线宽度
     * @param candleLineWidth
     */
    open fun setCandleLineWidth(candleLineWidth: Float) {
        mMainDraw.setCandleLineWidth(candleLineWidth)
    }

    /**
     * 蜡烛是否空心
     */
    open fun setCandleSolid(candleSolid: Boolean) {
        mMainDraw.setCandleSolid(candleSolid)
    }

    open fun setRSI1Color(color: Int) {
        mRSIDraw.setRSI1Color(color)
    }

    open fun setRSI2Color(color: Int) {
        mRSIDraw.setRSI2Color(color)
    }

    open fun setRSI3Color(color: Int) {
        mRSIDraw.setRSI3Color(color)
    }

    open fun setWR1Color(color: Int) {
        mWRDraw.setR1Color(color)
    }

    open fun setWR2Color(color: Int) {
        mWRDraw.setR2Color(color)
    }

    open fun setWR3Color(color: Int) {
        mWRDraw.setR3Color(color)
    }

    override fun setTextSize(textSize: Float) {
        super.setTextSize(textSize)
        mMainDraw.setMATextSize(textSize)
        mMainDraw.setBOLLTextSize(textSize)
        mBOLLDraw.setTextSize(textSize)
        mRSIDraw.setTextSize(textSize)
        mWRDraw.setTextSize(textSize)
        mMACDDraw.setTextSize(textSize)
        mKDJDraw.setTextSize(textSize)
        mVolumeDraw.setTextSize(textSize)
    }

    override fun setLineWidth(lineWidth: Float) {
        super.setLineWidth(lineWidth)
        mMainDraw.setMALineWidth(lineWidth)
        mMainDraw.setBOLLLineWidth(lineWidth)
        mBOLLDraw.setLineWidth(lineWidth)
        mRSIDraw.setLineWidth(lineWidth)
        mWRDraw.setLineWidth(lineWidth)
        mMACDDraw.setLineWidth(lineWidth)
        mKDJDraw.setLineWidth(lineWidth)
        mVolumeDraw.setLineWidth(lineWidth)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        mMainDraw.setSelectorTextColor(color)
    }

    /**
     * 设置刷新监听
     */
    open fun setRefreshListener(refreshListener: KChartRefreshListener) {
        mRefreshListener = refreshListener
    }

    var mainDrawIndicator: String
        get() {
            return mMainDraw.mainDrawIndicator
        }
        set(value) {
            if (mMainDraw.mainDrawIndicator != value) {
                mMainDraw.mainDrawIndicator = value
                invalidate()
            }
        }


    /**
     * 使用蜡烛图或者分时图，需要注意的是吗，如果KChartView还未完成初始化的时候，需要将参数isNeedCheck设置成false，来避免空指针
     * @param isMain true表示使用蜡烛图，false表示使用分时图
     * @param isNeedCheck 是否检查当前主视图和设置的是否一致，减少重绘
     */
    fun useMainOrMin(isMain: Boolean, isNeedCheck: Boolean = true) {
        if (isMain) {
            if (!isNeedCheck || getMainDraw() != mMainDraw) {
                setMainDraw(mMainDraw)
                setVolumeDraw(mVolumeDraw)
                invalidate()
            }
        } else {
            if (!isNeedCheck || getMainDraw() != mMinuteChartDraw) {
                setMainDraw(mMinuteChartDraw)
                setVolumeDraw(mVolumeMinDraw)
                invalidate()
            }
        }

    }

}
