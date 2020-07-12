package br.com.mmetzner.vendor.helper.route

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.*
import br.com.mmetzner.vendor.repository.FirebaseData
import kotlin.collections.HashMap

class StartRouteViewModel : ViewModel() {

    val mTruckId: MutableLiveData<String> = MutableLiveData()
    val mProducts: MutableLiveData<List<Product?>> = MutableLiveData()
    val mSuggestedProducts: MutableLiveData<List<Product?>> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val allRequestsReady: MutableLiveData<Boolean> = MutableLiveData()

    private fun updateTruck(
        truckId: String?,
        updates: HashMap<String, Any>
    ) {
        FirebaseData.updateTruck(
            truckId = truckId!!,
            updates = updates,
            successCallBack = {
                allRequestsReady.postValue(true)
            },
            errorCallBack = {
                error.postValue(it)
            }
        )
    }

    fun startRoute(
        items: List<Product?>,
        latitude: Double,
        longitude: Double
    ) {
        val products = items
            .map { ProductItemRequest(it?.id, it?.quantity ?: 0) }

        val updates: HashMap<String, Any> = hashMapOf(
            "routeStarted" to true,
            "products" to products,
            "latitude" to latitude,
            "longitude" to longitude
        )

        updateTruck(mTruckId.value, updates)
    }

    fun mountSuggestedList(suggestedQuantities: List<Product>) {
        suggestedQuantities.forEach {
            val sugestedProduct =
                mProducts.value?.first { product -> product?.description.equals(it.description) }
            sugestedProduct?.quantity = it.quantity
        }
        mSuggestedProducts.postValue(suggestedQuantities)
    }


}