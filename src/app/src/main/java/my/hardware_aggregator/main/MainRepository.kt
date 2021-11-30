package my.hardware_aggregator.main

import android.content.Context

import retrofit2.http.POST
import retrofit2.http.Body

import my.hardware_aggregator.data.models.ParserRequest
import my.hardware_aggregator.data.models.DataResponse

import my.hardware_aggregator.util.Resource


interface MainRepository {

    @POST("/")
    suspend fun loadProductsFromParser(@Body info: ParserRequest): Resource<DataResponse>

    fun getProductsFromDB(context: Context, hardwareType: String): Resource<DataResponse>

}