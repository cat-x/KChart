package xyz.a1api.kchart.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

/**
 * Created by Cat-x on 2018/8/7.
 * For KChart
 * Cat-x All Rights Reserved
 */

/**
 * 是否全屏
 * @receiver Activity
 * @param isFull Boolean
 */
fun Activity.setFullScreen(isFull: Boolean) {
    window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility == View.SYSTEM_UI_FLAG_LOW_PROFILE || visibility == View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_IMMERSIVE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.setOnSystemUiVisibilityChangeListener(null)
        } else if (visibility == View.SYSTEM_UI_FLAG_VISIBLE || visibility == View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_IMMERSIVE) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.setOnSystemUiVisibilityChangeListener(null)
        }
    }
    if (isFull) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_IMMERSIVE
    } else {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_IMMERSIVE
    }
    window.decorView.requestLayout()
}

/**
 * 是否保持亮屏
 * @receiver Activity
 * @param enabled Boolean
 */
fun Activity.setKeepScreenOn(enabled: Boolean) {
    if (enabled) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

/**
 * 设置屏幕旋转状态
 * @receiver Activity
 * @param rotation Int (1：默认方向感应；2：锁死横竖屏，但可以自动切换正反；3，锁死竖屏，能切换正反；4，锁死横屏，能切换正反；
 * 5：锁死横竖屏，不能切换正反；6，锁死竖屏，不能切换正反；7，锁死横屏，不能切换正反；8，切换横竖屏，不锁死正反；9，切换横竖屏，锁死正反)
 */
fun Activity.setRotation(rotation: Int) {
    when (rotation) {

        // Rotation free
        1 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        // Lock in current rotation
        2 -> {
            val currentOrientation = resources.configuration.orientation
            setRotation(if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) 3 else 4)
        }
        // Lock in portrait
        3 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        // Lock in landscape
        4 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        // Lock in current rotation
        5 -> {
            val currentOrientation = resources.configuration.orientation
            setRotation(if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) 5 else 6)
        }
        // Lock in portrait
        6 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Lock in landscape
        7 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        8 -> {
            val currentOrientation = resources.configuration.orientation
            setRotation(if (currentOrientation != Configuration.ORIENTATION_PORTRAIT) 3 else 4)
        }
        9 -> {
            val currentOrientation = resources.configuration.orientation
            setRotation(if (currentOrientation != Configuration.ORIENTATION_PORTRAIT) 5 else 6)
        }
    }
}

/**
 * 获取状态栏高度，当获取不到是，默认高度为20dp
 * @return Int 状态栏的px高度
 */
fun Activity.getBarHeight(): Int {
    var statusBarHeight = dip2px(20)

    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)  //应用区域
    val statusBar = getWindowHeight() - rect.height()  //状态栏高度=屏幕高度-应用区域高度
    if (statusBar == 0) {
        //获取status_bar_height资源的ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
    } else {
        statusBarHeight = statusBar
    }
    return statusBarHeight
}

fun Activity.getWindowHeight(): Int {
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

fun Activity.getContentHeight(): Int {
    return getWindowHeight() - getBarHeight()
}

