package xyz.a1api.kchart.utils

import android.content.Context
import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.text.DecimalFormat

/**
 * Created by Cat-x on 2018/11/27.
 * For KChart
 * Cat-x All Rights Reserved
 */
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dipF(value: Int): Float = (value * resources.displayMetrics.density)
fun Context.dipF(value: Float): Float = (value * resources.displayMetrics.density)

fun Context.getResColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(
        this, id
    )
}

var android.widget.TextView.textResColor: Int
    @Deprecated(
        "Property does not have a getter",
        level = DeprecationLevel.ERROR
    ) get() = throw Exception("Property does not have a getter")
    set(v) = setTextColor(context.getResColor(v))


fun Context.dip2px(value: Int): Int {
    return Math.round(value * resources.displayMetrics.density);
}

fun Context.dip2px(value: Float): Int {
    return Math.round(value * resources.displayMetrics.density);
}

fun View.setNegateVisibility(
    show: (View) -> Unit = {},
    hide: (View) -> Unit = {},
    isGone: Boolean = true
) {
    if (visibility == View.VISIBLE) {
        hide(this)
        visibility = if (isGone) View.GONE else View.INVISIBLE
    } else {
        show(this)
        visibility = View.VISIBLE
    }
}

fun Float.toFix(number: Int = 2): String {
//    var preString = "1"
//    for (i in 1..number) {
//        preString += "0"
//    }
//    val preNum = preString.toFloat()
//    val num  =  Math.round(this * preNum) / preNum
//    return num.toString()

    var preString = "0."
    for (i in 1..number) {
        preString += "0"
    }
    return DecimalFormat(preString).format(this)
}

fun Double.toFix(number: Int = 2): String {
    var preString = "0."
    for (i in 1..number) {
        preString += "0"
    }
    return DecimalFormat(preString).format(this)
}


fun Float.toPercentage(): String {
    return DecimalFormat("#.##%").format(this)
}

fun Double.toPercentage(): String {
    return DecimalFormat("#.##%").format(this)
}

fun Paint.getXAlignRight(originalX: Int, content: String): Float {
    return getXAlignRight(originalX.toFloat(), content)
}

fun Paint.getXAlignRight(originalX: Float, content: String): Float {
    return originalX - measureText(content)
}