package my.hardware_aggregator.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import my.hardware_aggregator.data.models.DataResponse
import my.hardware_aggregator.data.models.ParserRequest
import my.hardware_aggregator.util.Resource

class MainViewModel(application: Application): AndroidViewModel(application) {

    sealed class Event {
        class SuccessParser(val dataResponse: DataResponse): Event()
        class SuccessDB(val dataResponse: DataResponse): Event()

        class Error(val message: String): Event()

        object Loading: Event()
        object Empty: Event()
    }

    private val repository = DefaultMainRepository()

    private val _state = MutableStateFlow<Event> (Event.Empty)
    val state: StateFlow<Event> = _state

    fun loadProductsFromParser(hardware: String, useShop: Boolean, useForcecom: Boolean, useTomas: Boolean) {

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = Event.Loading

            when(val response = repository.loadProductsFromParser(ParserRequest(hardware, useShop, useForcecom, useTomas))) {

                is Resource.SuccessParser -> { _state.value = Event.SuccessParser(response.data!!) }
                is Resource.Error -> { _state.value = Event.Error(response.message!!) }
            }
        }

    }

    fun getProductsFromDB(context: Context, hardwareType: String) {

        viewModelScope.launch(Dispatchers.IO) {

            when(val products = repository.getProductsFromDB(context, hardwareType)) {

                is Resource.SuccessDB -> { _state.value = Event.SuccessDB(products.data!!) }
                is Resource.Error -> { _state.value = Event.Error(products.message!!) }



            }
        }
    }


}