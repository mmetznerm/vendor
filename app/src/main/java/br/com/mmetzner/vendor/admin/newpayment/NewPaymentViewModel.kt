package br.com.mmetzner.vendor.admin.newpayment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.PaymentRequest
import br.com.mmetzner.vendor.model.ProductRequest
import br.com.mmetzner.vendor.repository.FirebaseData

class NewPaymentViewModel : ViewModel() {

    val finishApp: MutableLiveData<Boolean> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun savePayment(
        description: String,
        days: Int,
        isCharge: Boolean
    ) {
        loadingProgress.postValue(true)
        FirebaseData.savePayment(
            paymentRequest = PaymentRequest(description, days, isCharge),
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

}