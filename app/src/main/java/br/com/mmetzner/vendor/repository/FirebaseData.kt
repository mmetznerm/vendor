package br.com.mmetzner.vendor.repository

import android.util.Log
import br.com.mmetzner.vendor.model.*
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseData {
    private const val DATABASE_USERS = "users"
    private const val DATABASE_TRUCKS = "trucks"
    private const val DATABASE_PRODUCTS = "products"
    private const val DATABASE_PAYMENTS = "payments"
    private const val DATABASE_CLIENTS = "clients"

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

    fun getAllProducts(successCallBack: (products: List<Product?>) -> Unit, errorCallBack: (error: String) -> Unit) {
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

    fun getAllClients(successCallBack: (users: List<Client?>) -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_CLIENTS)
            .get()
            .addOnSuccessListener { result ->
                val clients = mutableListOf<Client?>()
                result.documents.forEach {
                    val client = it.toObject(Client::class.java)
                    client?.id = it.id
                    clients.add(client)
                }

                successCallBack.invoke(clients)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }

    fun saveClient(client: ClientRequest, successCallBack: () -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_CLIENTS)
            .document()
            .set(client)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }

    fun saveProduct(product: ProductRequest, successCallBack: () -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_PRODUCTS)
            .document()
            .set(product)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }

    fun savePayment(paymentRequest: PaymentRequest, successCallBack: () -> Unit, errorCallBack: (error: String) -> Unit) {
        db.collection(DATABASE_PAYMENTS)
            .document()
            .set(paymentRequest)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(exception.localizedMessage ?: exception.message ?: "Generic Error")
                Log.d("Vendor", "Error", exception)
            }
    }
}