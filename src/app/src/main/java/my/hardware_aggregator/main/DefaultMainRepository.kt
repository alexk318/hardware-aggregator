package my.hardware_aggregator.main

import android.content.Context

import my.hardware_aggregator.AppModule
import my.hardware_aggregator.data.models.ParserRequest
import my.hardware_aggregator.data.models.DataResponse
import my.hardware_aggregator.data.models.Product
import my.hardware_aggregator.db.DBHelper
import my.hardware_aggregator.util.Resource

import java.lang.Exception

class DefaultMainRepository : MainRepository {

    override suspend fun loadProductsFromParser(info: ParserRequest): Resource<DataResponse> {

        return try {
            val response = AppModule.provideParserApi().loadData(info)

            var productsShop = response.body()?.shop
            if (productsShop == null) { productsShop = ArrayList<Product>() }

            var productsForcecom = response.body()?.forcecom
            if (productsForcecom == null) { productsForcecom = ArrayList<Product>() }

            var productsTomas = response.body()?.tomas
            if (productsTomas == null) { productsTomas = ArrayList<Product>() }

            if (response.isSuccessful) {
                Resource.SuccessParser(DataResponse(productsShop, productsForcecom, productsTomas))
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }

    }

    override fun getProductsFromDB(context: Context, hardwareType: String): Resource<DataResponse> {
        return try {
            val dbHelper = DBHelper(context)

            val productsShop = dbHelper.DBManagerShop().getSpecificProducts(hardwareType)
            val productsForcecom = dbHelper.DBManagerForcecom().getSpecificProducts(hardwareType)
            val productsTomas = dbHelper.DBManagerTomas().getSpecificProducts(hardwareType)

            if (productsShop.isEmpty() and productsForcecom.isEmpty() and productsTomas.isEmpty()) {
                Resource.Error("No saved data found")
            } else {
                Resource.SuccessDB(DataResponse(productsShop, productsForcecom, productsTomas))
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

}