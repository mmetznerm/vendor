package br.com.mmetzner.vendor.repository

import android.util.Log
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.model.Truck
import br.com.mmetzner.vendor.model.User
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseData {
    private const val DATABASE_USERS = "users"
    private const val DATABASE_TRUCKS = "trucks"
    private const val DATABASE_PRODUCTS = "products"

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

    fun getEnableTrucks(successCallBack: (trucks: List<Truck?>) -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_TRUCKS)
            .whereEqualTo("routeStarted", true)
            .get()
            .addOnSuccessListener { result ->
                val trucks = mutableListOf<Truck?>()
                result.documents.forEach {
                    val truck = it.toObject(Truck::class.java)
                    truck?.id = it.id
                    trucks.add(truck)
                }

                successCallBack.invoke(trucks)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getAllProducts(successCallBack: (users: List<Product?>) -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_PRODUCTS)
            .get()
            .addOnSuccessListener { result ->
                val products = mutableListOf<Product?>()
                result.documents.forEach {
                    val product = it.toObject(Product::class.java)
                    product?.id = it.id
                    products.add(product)
                }

                successCallBack.invoke(products)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }
}