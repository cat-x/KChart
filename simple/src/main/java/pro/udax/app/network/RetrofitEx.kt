package pro.udax.app.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Cat-x on 2018/11/29.
 * For KChart
 * Cat-x All Rights Reserved
 */
/**
 * 执行网络请求任务
 * @receiver Call<T>
 * @param success (data: T?, body: Response<T>) -> Unit 成功回调
 * @param fail (t: Throwable) -> Unit 失败回调
 * @param failNumberOfRetries Int 失败重试次数，默认为0
 */
fun <T> Call<T>.done(
    success: (data: T?, body: Response<T>) -> Unit,
    fail: (t: Throwable) -> Unit = {},
    failNumberOfRetries: Int = 0
) {

    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            Log.i("onResponse", response.body().toString())
            if (response.body() == null || !response.isSuccessful) {
                onFailure(
                    call,
                    Throwable("response.body() is null", Throwable(response.code().toString()))
                )
            } else {
                success(response.body(), response)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (failNumberOfRetries > 0) {
                call.clone().done<T>(success, fail, failNumberOfRetries - 1)
            } else {
                fail(t)
            }
        }

    })
}

