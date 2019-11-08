package xyz.a1api.kchart.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import xyz.a1api.kchart.R
import xyz.a1api.kchart.utils.getResColor
import xyz.a1api.kchart.utils.isInvisible

/**
 * Created by Cat-x on 2019/10/30.
 * For KChart
 * Cat-x All Rights Reserved
 */
open class TimeStepView : RelativeLayout {
    protected var isMinute: Boolean = false

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
        val view = View.inflate(context, R.layout.kchart_time_step_view, this)
        val timeStepTextView = view.findViewById<AppCompatTextView>(R.id.timeStepTextView)
        val timeStepLineView = view.findViewById<View>(R.id.timeStepLineView)

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.TimeStepView)
            if (array != null) {
                try {
                    isMinute = array.getBoolean(R.styleable.TimeStepView_isMinute, false)
                    val lineColor =
                        array.getColor(
                            R.styleable.TimeStepView_lineColor,
                            context.getResColor(R.color.time_step_view_line_color)
                        )
                    val textColor =
                        array.getColor(
                            R.styleable.TimeStepView_textColor,
                            context.getResColor(R.color.time_step_view_text_color)
                        )
                    val textSize =
                        array.getDimension(
                            R.styleable.TimeStepView_textSize,
                            resources.getDimension(R.dimen.time_step_view_text_size)
                        )
                    val text = array.getString(R.styleable.TimeStepView_text)


                    timeStepLineView.setBackgroundColor(lineColor)
                    timeStepTextView.setTextColor(textColor)
                    timeStepTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    timeStepTextView.setText(text)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                array.recycle()
            }
        }
    }


    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        findViewById<View>(R.id.timeStepLineView)?.isInvisible = !selected
    }

    fun isTimeMinute(): Boolean {
        return isMinute
    }
}