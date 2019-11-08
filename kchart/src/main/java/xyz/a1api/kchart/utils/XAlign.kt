package xyz.a1api.kchart.utils

/**
 * x轴方向的对齐方式
 * Created by tifezh on 2018/4/2.
 * For KChart
 * Cat-x All Rights Reserved
 */
sealed class XAlign {
    object LEFT : XAlign()
    object CENTER : XAlign()
    object RIGHT : XAlign()
}
