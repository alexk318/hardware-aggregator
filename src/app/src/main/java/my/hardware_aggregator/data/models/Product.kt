package my.hardware_aggregator.data.models


import java.io.Serializable

data class Product (

    val title: String?,
    val description: String?,

    val cost: Int?,

    val image_url: String?,
    val url: String?

) : Serializable
