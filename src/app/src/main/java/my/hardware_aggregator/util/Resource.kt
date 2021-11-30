package my.hardware_aggregator.util

sealed class Resource<T> (val data: T?, val message: String?) {

    class SuccessParser<T> (data: T) : Resource<T>(data, null)
    class SuccessDB<T> (data: T) : Resource<T>(data, null)

    class Error<T> (message: String) : Resource<T>(null, message)

}