package xyz.a1api.kchart.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.a1api.kchart.R
import xyz.a1api.kchart.entity.ICandleBoll
import xyz.a1api.kchart.entity.IKLine
import xyz.a1api.kchart.utils.getResColor
import xyz.a1api.kchart.view.KChartView

/**
 * Created by Cat-x on 2019/10/30.
 * For KChart
 * Cat-x All Rights Reserved
 */
open class IndicatorTextView : AppCompatTextView {

    protected var selectColor: Int = 0
    protected var unSelectColor: Int = 0
    protected var nameText: String = ""

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

    protected fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.IndicatorTextView)
            if (array != null) {

                try {
                    selectColor =
                        array.getColor(
                            R.styleable.IndicatorTextView_selectColor,
                            context.getResColor(R.color.indicator_select_color)
                        )
                    unSelectColor =
                        array.getColor(
                            R.styleable.IndicatorTextView_unSelectColor,
                            context.getResColor(R.color.indicator_unSelect_color)
                        )
                    nameText =
                        array.getString(R.styleable.IndicatorTextView_nameText) ?: text?.toString()
                                ?: ""
                    setTextColor(unSelectColor)

                    if (nameText.isNotBlank()) {
                        setText(nameText)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                array.recycle()
            }
        }
    }


    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            setTextColor(selectColor)
        } else {
            setTextColor(unSelectColor)
        }
    }

    open fun setClickMainIndicator(
        kChartView: KChartView,
        name: String = "",
        action: (indicatorName: String) -> Unit
    ) {
        if (name.isNotBlank()) {
            nameText = name
        }
        setOnClickListener() {
            isSelected = true
            ICandleBoll.IMainConfig.useMainDrawIndicator = nameText
            kChartView.mainDrawIndicator = nameText
            action(nameText)
        }
    }

    open fun setClickSecondIndicator(
        kChartView: KChartView,
        name: String = "",
        action: (indicatorName: String) -> Unit
    ) {
        if (name.isNotBlank()) {
            nameText = name
        }
        setOnClickListener() {
            isSelected = true
            IKLine.ISecondDrawConfig.useSecondDrawIndicator = nameText
            kChartView.secondDrawIndicator = nameText
            action(nameText)
        }
    }
}
