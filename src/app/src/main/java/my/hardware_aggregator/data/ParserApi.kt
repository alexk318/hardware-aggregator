package my.hardware_aggregator.data

import my.hardware_aggregator.data.models.ParserRequest
import my.hardware_aggregator.data.models.DataResponse

import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Response

interface ParserApi {

    @POST("/")
    suspend fun loadData(@Body info: ParserRequest): Response<DataResponse>

}