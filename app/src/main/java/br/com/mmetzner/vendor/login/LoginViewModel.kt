package br.com.mmetzner.vendor.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mmetzner.vendor.model.User
import br.com.mmetzner.vendor.model.UserType
import br.com.mmetzner.vendor.repository.FirebaseData

class LoginViewModel : ViewModel() {

    val adminTask: MutableLiveData<User> = MutableLiveData()
    val helperTask: MutableLiveData<User> = MutableLiveData()
    val loadingProgress: MutableLiveData<Boolean> = MutableLiveData()
    val userNotFound: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun getUserByEmailAndPassword(email: String, password: String) {
        loadingProgress.postValue(true)
        FirebaseData.getUserByEmailAndPassword(
            email = email,
            password = password,
            successCallBack = {
                loadingProgress.postValue(false)
                checkUserType(it)
            },
            errorCallBack = {
                loadingProgress.postValue(false)
                error.postValue(it)
            }
        )
    }

    private fun checkUserType(user: User?) {
        if(user != null) {
            if(user.type == UserType.ADMIN.ordinal) {
                adminTask.postValue(user)
            } else {
                helperTask.postValue(user)
            }
        } else {
            userNotFound.postValue(true)
        }
    }

}