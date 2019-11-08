package pro.udax.app.network.entry.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Cat-x on 2018/11/30.
 * For KChart
 * Cat-x All Rights Reserved
 */
data class Data<T>(
    @SerializedName("code")
    val code: Int = 0, // 200
    @SerializedName("data")
    val `data`: T?,
    @SerializedName("msg")
    val msg: String = "" // Successful request

)