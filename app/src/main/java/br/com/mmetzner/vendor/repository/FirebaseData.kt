package br.com.mmetzner.vendor.repository

import android.util.Log
import br.com.mmetzner.vendor.model.User
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseData {
    private const val DATABASE_USERS = "users"

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUserByEmailAndPassword(email: String, password: String, successCallBack: (users: User?) -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_USERS)
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                val user = result.documents.map { it.toObject(User::class.java) }.firstOrNull()
                successCallBack.invoke(user)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getAllUsers(successCallBack: (users: List<User?>) -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_USERS)
            .get()
            .addOnSuccessListener { result ->
                successCallBack.invoke(result.documents.map { it.toObject(User::class.java) })
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }
}