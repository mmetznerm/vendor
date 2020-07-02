package br.com.mmetzner.vendor.admin.neworder.product

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.*
import br.com.mmetzner.vendor.repository.FirebaseData
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

class SelectProductViewModel : ViewModel() {

    val mTruckSelected: MutableLiveData<Truck> = MutableLiveData()
    val mClient: MutableLiveData<Client?> = MutableLiveData()
    val mTrucks: MutableLiveData<List<Truck?>> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val finishApp: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun calculateDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1: Double = StartP.latitude
        val lat2: Double = EndP.latitude
        val lon1: Double = StartP.longitude
        val lon2: Double = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return Radius * c
    }

    fun calculateTruckBeforeSendOrder() {
        var truckNearAddress: Pair<Double, Truck>? = null
        val client = mClient.value!!

        mTrucks.value?.forEach {
            val distance = calculateDistance(
                LatLng(client.latitude, client.longitude),
                LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0)
            )
            if(truckNearAddress == null) {
                truckNearAddress = Pair(distance, it!!)
            } else {
                if(truckNearAddress!!.first > distance ) {
                    truckNearAddress = Pair(distance, it!!)
                }
            }
        }

        mTruckSelected.postValue(truckNearAddress!!.second)
    }

    fun sendOrder(
        client: Client,
        truck: Truck,
        products: List<Product>?,
        date: String
    ) {
        loadingProgress.postValue(true)
        FirebaseData.saveOrder(
            orderRequest = OrderRequest(client.id, truck.id, convertProducts(products), date),
            successCallBack = {
                loadingProgress.postValue(false)
                finishApp.postValue(true)

            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    private fun convertProducts(products: List<Product>?): List<ProductItemRequest>? {
        return products
            ?.filter { it.quantity > 0 }
            ?.map { ProductItemRequest(it.id, it.quantity) }
    }

}