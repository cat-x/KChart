package xyz.a1api.kchart.utils

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi

/**
 * Created by Cat-x on 2019/10/31.
 * For KChart
 * Cat-x All Rights Reserved
 */
inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }


/**
 * Add an action which will be invoked before the text changed.
 *
 * @return the [TextWatcher] added to the TextView
 */
inline fun TextView.doBeforeTextChanged(
    crossinline action: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit
) = addTextChangedListener(beforeTextChanged = action)

/**
 * Add an action which will be invoked when the text is changing.
 *
 * @return the [TextWatcher] added to the TextView
 */
inline fun TextView.doOnTextChanged(
    crossinline action: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit
) = addTextChangedListener(onTextChanged = action)

/**
 * Add an action which will be invoked after the text changed.
 *
 * @return the [TextWatcher] added to the TextView
 */
inline fun TextView.doAfterTextChanged(
    crossinline action: (text: Editable?) -> Unit
) = addTextChangedListener(afterTextChanged = action)

/**
 * Add a text changed listener to this TextView using the provided actions
 *
 * @return the [TextWatcher] added to the TextView
 */
inline fun TextView.addTextChangedListener(
    crossinline beforeTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline onTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline afterTextChanged: (text: Editable?) -> Unit = {}
): TextWatcher {
    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s)
        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(text, start, count, after)
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(text, start, before, count)
        }
    }
    addTextChangedListener(textWatcher)

    return textWatcher
}


var View.leftPadding: Int
    inline get() = paddingLeft
    set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

var View.topPadding: Int
    inline get() = paddingTop
    set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

var View.rightPadding: Int
    inline get() = paddingRight
    set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

var View.bottomPadding: Int
    inline get() = paddingBottom
    set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)

/**
 * Align the current View's start edge with another child's start edge.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun RelativeLayout.LayoutParams.alignStart(@IdRes id: Int): Unit =
    addRule(RelativeLayout.ALIGN_START, id)

/**
 * Align the current View's end edge with another child's end edge.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun RelativeLayout.LayoutParams.alignEnd(@IdRes id: Int): Unit =
    addRule(RelativeLayout.ALIGN_END, id)

/**
 * Align the current View's top edge with its parent's top edge.
 */
inline fun RelativeLayout.LayoutParams.alignParentTop() = addRule(RelativeLayout.ALIGN_PARENT_TOP)

/**
 * Align the current View's right edge with its parent's right edge.
 */
inline fun RelativeLayout.LayoutParams.alignParentRight() =
    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

/**
 * Align the current View's bottom edge with its parent's bottom edge.
 */
inline fun RelativeLayout.LayoutParams.alignParentBottom() =
    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

/**
 * Align the current View's left edge with its parent's left edge.
 */
inline fun RelativeLayout.LayoutParams.alignParentLeft() = addRule(RelativeLayout.ALIGN_PARENT_LEFT)

/**
 * Center the child horizontally in its parent.
 */
inline fun RelativeLayout.LayoutParams.centerHorizontally() =
    addRule(RelativeLayout.CENTER_HORIZONTAL)

/**
 * Center the child vertically in its parent.
 */
inline fun RelativeLayout.LayoutParams.centerVertically() = addRule(RelativeLayout.CENTER_VERTICAL)

/**
 * Center the child horizontally and vertically in its parent.
 */
inline fun RelativeLayout.LayoutParams.centerInParent() = addRule(RelativeLayout.CENTER_IN_PARENT)

/**
 * Align the current View's start edge with its parent's start edge.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun RelativeLayout.LayoutParams.alignParentStart(): Unit =
    addRule(RelativeLayout.ALIGN_PARENT_START)

/**
 * Align the current View's end edge with its parent's end edge.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun RelativeLayout.LayoutParams.alignParentEnd(): Unit =
    addRule(RelativeLayout.ALIGN_PARENT_END)

