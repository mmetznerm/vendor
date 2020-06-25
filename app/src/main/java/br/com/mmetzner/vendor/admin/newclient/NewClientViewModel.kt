package br.com.mmetzner.vendor.admin.newclient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.ClientRequest
import br.com.mmetzner.vendor.repository.FirebaseData

class NewClientViewModel : ViewModel() {

    val finishApp: MutableLiveData<Boolean> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun saveClient(
        name: String,
        address: String,
        cpfCnpj: String,
        city: String,
        phone: String,
        latitude: Double?,
        longitude: Double?
    ) {
        loadingProgress.postValue(true)
        FirebaseData.saveClient(
            client = ClientRequest(name, address, cpfCnpj, phone, city, latitude, longitude),
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