package xyz.a1api.kchart.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import xyz.a1api.kchart.view.ScrollAndScaleView

/**
 * Created by Cat-x on 2018/12/11.
 * For KChart
 * Cat-x All Rights Reserved
 */
class KScrollView : ScrollView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
    }

    var scaleView: ScrollAndScaleView? = null

    init {
        /**
         * 在选中和长按时，不允许滑动
         */
        setOnTouchListener { _, _ ->
            if (scaleView?.isHadSelect == true || scaleView?.isLongPress == true) {
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }
}
