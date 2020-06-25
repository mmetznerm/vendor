package br.com.mmetzner.vendor.admin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.model.Truck
import br.com.mmetzner.vendor.repository.FirebaseData

class MapViewModel : ViewModel() {

    val trucks: MutableLiveData<List<Truck?>> = MutableLiveData()
    val truckDetailPopup: MutableLiveData<Pair<String?, String>> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun getEnableTrucks() {
        FirebaseData.getEnableTrucks(
            successCallBack = { trucks.postValue(it) },
            errorCallBack = { error.postValue(it) }
        )
    }

    private fun filterTruckByLicensePlate(licensePlate: String?): Truck? {
        return trucks.value?.first { it?.licensePlate == licensePlate }
    }

    fun getProductsDetailByTruck(licensePlate: String?) {
        val truck = filterTruckByLicensePlate(licensePlate)

        loadingProgress.postValue(true)
        FirebaseData.getAllProducts(
            successCallBack = {
                loadingProgress.postValue(false)
                setProductsName(it, truck)
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    private fun setProductsName(products: List<Product?>, truck: Truck?) {
        truck?.products?.forEach { truckProduct ->
            val product = products.first { it?.id == truckProduct.productId }
            truckProduct.productDescription = product?.description
        }

        val message = StringBuilder()
        truck?.products?.forEach {
            message.append("${it.productDescription} - ${it.productQuantity}\n")
        }

        truckDetailPopup.postValue(Pair(truck?.licensePlate, message.toString()))
    }

}