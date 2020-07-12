package br.com.mmetzner.vendor.helper.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.Charge
import br.com.mmetzner.vendor.model.ChargeRequest
import br.com.mmetzner.vendor.model.Order
import br.com.mmetzner.vendor.model.Payment
import br.com.mmetzner.vendor.repository.FirebaseData
import br.com.mmetzner.vendor.utils.CustomDate
import java.util.*

class DetailViewModel : ViewModel() {

    val mOrder: MutableLiveData<Order> = MutableLiveData()
    val mCharges: MutableLiveData<List<Charge?>> = MutableLiveData()
    val mPayments: MutableLiveData<List<Payment?>> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val addChargeRequestReady: MutableLiveData<Boolean> = MutableLiveData()
    val updateChargesRequestReady: MutableLiveData<Boolean> = MutableLiveData()
    val finalizeRequestReady: MutableLiveData<Boolean> = MutableLiveData()
    val allRequestsReady: MutableLiveData<Boolean> = MutableLiveData()

    fun getCharges() {
        loadingProgress.postValue(true)
        FirebaseData.getCharges(
            clientId = mOrder.value?.clientId!!,
            successCallBack = {
                loadingProgress.postValue(false)
                mCharges.postValue(it)
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    fun getPayments() {
        loadingProgress.postValue(true)
        FirebaseData.getPayments(
            successCallBack = {
                loadingProgress.postValue(false)
                mPayments.postValue(it)
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    private fun updateCharges(charges: List<Charge?>) {
        FirebaseData.updateCharges(
            charges = charges,
            successCallBack = {
                updateChargesRequestReady.value = true
                checkAllRequests()
            },
            errorCallBack = {
                error.postValue(it)
            }
        )
    }

    private fun addCharge(charge: ChargeRequest) {
        FirebaseData.saveCharge(
            charge = charge,
            successCallBack = {
                addChargeRequestReady.value = true
                checkAllRequests()
            },
            errorCallBack = {
                error.postValue(it)
            }
        )
    }

    private fun finalizeOrder(orderId: String?) {
        val updates = hashMapOf<String, Any>(
            "finished" to true
        )

        FirebaseData.updateOrder(
            orderId = orderId!!,
            updates = updates,
            successCallBack = {
                finalizeRequestReady.value = true
                checkAllRequests()
            },
            errorCallBack = {
                error.postValue(it)
            }
        )
    }

    fun checkOrderBeforeFinalize(charges: List<Charge?>, payment: Payment?) {
        val orderId = mOrder.value?.id
        val clientId = mOrder.value?.clientId
        val total = calculateTotalProducts()?.plus(calculateTotalCharges(charges))
        val date = CustomDate.addDayDoCurrentDate(Calendar.getInstance(), payment?.days ?: 0)

        if(payment?.charge == true) {
            addCharge(ChargeRequest(clientId, orderId, total, date))
        } else {
            addChargeRequestReady.value = true
        }

        val chargesSelected = charges.filter { it?.selected == true }
        if(chargesSelected.isNotEmpty()) {
            updateCharges(chargesSelected)
        } else {
            updateChargesRequestReady.value = true
        }
        finalizeOrder(orderId)
    }

    private fun calculateTotalProducts(): Double? {
        return mOrder.value?.productList?.map { it!!.quantity * it.price }?.sum()
    }

    private fun calculateTotalCharges(charges: List<Charge?>): Double {
        return charges.filter { it?.selected == true }.sumByDouble { it?.value ?: 0.0 }
    }

    fun getTotalOrder(charges: List<Charge?>): Double? {
        return calculateTotalProducts()?.plus(calculateTotalCharges(charges))
    }

    private fun checkAllRequests() {
        if(addChargeRequestReady.value == false) {
            return
        }
        if(updateChargesRequestReady.value == false) {
            return
        }
        if(finalizeRequestReady.value == false) {
            return
        }
        allRequestsReady.postValue(true)
    }

}