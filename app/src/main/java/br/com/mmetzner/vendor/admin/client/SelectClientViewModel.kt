package br.com.mmetzner.vendor.admin.client

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.Client
import br.com.mmetzner.vendor.model.ProductRequest
import br.com.mmetzner.vendor.repository.FirebaseData

class SelectClientViewModel : ViewModel() {

    val clients: MutableLiveData<List<Client?>> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun getClients() {
        loadingProgress.postValue(true)
        FirebaseData.getAllClients(
            successCallBack = {
                loadingProgress.postValue(false)
                clients.postValue(it)

            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

}