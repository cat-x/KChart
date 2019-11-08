package pro.udax.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Cat-x on 2018/11/29.
 * For KChart
 * Cat-x All Rights Reserved
 */
object RetrofitTools {


    var retrofit = Retrofit.Builder()
        .baseUrl("xxxxx")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var apiService: ApiService = retrofit.create<ApiService>(ApiService::class.java)


}