package br.com.mmetzner.vendor.helper.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.*
import br.com.mmetzner.vendor.repository.FirebaseData

class OrderViewModel : ViewModel() {

    val mTruck: MutableLiveData<Truck> = MutableLiveData()
    val mUser: MutableLiveData<User> = MutableLiveData()
    val mProducts: MutableLiveData<List<Product?>> = MutableLiveData()
    val mClients: MutableLiveData<List<Client?>> = MutableLiveData()
    val mOrders: MutableLiveData<List<Order?>> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val allRequestsReady: MutableLiveData<Boolean> = MutableLiveData()

    fun getTruck() {
        loadingProgress.postValue(true)
        FirebaseData.getTruckById(
            truckId = mUser.value?.truckId!!,
            successCallBack = {
                loadingProgress.postValue(false)
                mTruck.postValue(it)
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    fun getProducts() {
        loadingProgress.postValue(true)
        FirebaseData.getAllProducts(
            successCallBack = {
                loadingProgress.postValue(false)
                mProducts.value = it
                checkAllRequests()
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    fun getClients() {
        loadingProgress.postValue(true)
        FirebaseData.getAllClients(
            successCallBack = {
                loadingProgress.postValue(false)
                mClients.value = it
                checkAllRequests()
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    fun getOrderByDay(date: String) {
        loadingProgress.postValue(true)
        FirebaseData.getOrderByDay(
            date = date,
            truckId = mUser.value?.truckId!!,
            successCallBack = {
                loadingProgress.postValue(false)
                mOrders.value = it
                checkAllRequests()
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    fun updateTruck() {
        val updates = hashMapOf<String, Any>(
            "routeStarted" to false,
            "products" to arrayListOf<Product>(),
            "latitude" to 0.0,
            "longitude" to 0.0
        )

        loadingProgress.postValue(true)
        FirebaseData.updateTruck(
            truckId = mUser.value?.truckId!!,
            updates = updates,
            successCallBack = {
                loadingProgress.postValue(false)
                getTruck()

            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    fun checkAllRequests() {
        if(mProducts.value == null) {
            return
        }
        if(mClients.value == null) {
            return
        }
        if(mOrders.value == null) {
            return
        }
        mountOrders()
    }

    private fun mountOrders() {
        val newProducts: MutableList<Product?> = mutableListOf()

        mOrders.value?.forEach { order ->
            val productsByOrder = order?.products
            productsByOrder?.forEach { p ->
                val product = mProducts.value?.first { it?.id == p.productId }
                product?.quantity = p.productQuantity
                newProducts.add(product)
            }
            order?.client = mClients.value?.first { it?.id == order?.clientId }
            order?.productList = newProducts.toList()
        }

        allRequestsReady.postValue(true)
    }

    fun getSuggestedQuantities(items: List<Order?>?): List<Product> {
        val products = mutableListOf<Product>()
        items?.forEach { it ->
            it?.productList?.groupBy { it?.description }?.forEach { product ->
                products.add(
                    Product(
                        description = product.key,
                        quantity = product.value.sumBy { it?.quantity ?: 0 })
                )
            }
        }
        return products.groupBy { it.description }
            .map { Product(description = it.key, quantity = it.value.sumBy { it.quantity }) }
    }

}