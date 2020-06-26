package br.com.mmetzner.vendor.admin.newproduct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.ProductRequest
import br.com.mmetzner.vendor.repository.FirebaseData

class NewProductViewModel : ViewModel() {

    val finishApp: MutableLiveData<Boolean> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun saveProduct(
        description: String,
        price: Double
    ) {
        loadingProgress.postValue(true)
        FirebaseData.saveProduct(
            product = ProductRequest(description, price),
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