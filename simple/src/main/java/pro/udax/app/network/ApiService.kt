package pro.udax.app.network


import pro.udax.app.network.entry.request.QuotationData
import pro.udax.app.network.entry.request.QuotationQueryReq
import pro.udax.app.network.entry.response.Data
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Cat-x on 2018/11/29.
 * For KChart
 * Cat-x All Rights Reserved
 */
interface ApiService {

    @POST("xxxxx")
    fun hisQuotation(@Body quotationQuery: QuotationQueryReq): Call<Data<QuotationData>>


}