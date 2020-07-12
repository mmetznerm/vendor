package br.com.mmetzner.vendor.repository

import android.util.Log
import android.widget.Toast
import br.com.mmetzner.vendor.model.*
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseData {
    private const val DATABASE_USERS = "users"
    private const val DATABASE_TRUCKS = "trucks"
    private const val DATABASE_PRODUCTS = "products"
    private const val DATABASE_PAYMENTS = "payments"
    private const val DATABASE_CLIENTS = "clients"
    private const val DATABASE_ORDERS = "orders"
    private const val DATABASE_CHARGES = "charges"

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUserByEmailAndPassword(
        email: String,
        password: String,
        successCallBack: (users: User?) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_USERS)
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                val user = result.documents.map { it.toObject(User::class.java) }.firstOrNull()
                successCallBack.invoke(user)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getEnableTrucks(
        successCallBack: (trucks: List<Truck?>) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
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
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getAllProducts(
        successCallBack: (products: List<Product?>) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
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
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getAllClients(
        successCallBack: (users: List<Client?>) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
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
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun saveClient(
        client: ClientRequest,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_CLIENTS)
            .document()
            .set(client)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun saveCharge(
        charge: ChargeRequest,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_CHARGES)
            .document()
            .set(charge)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun saveProduct(
        product: ProductRequest,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_PRODUCTS)
            .document()
            .set(product)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun savePayment(
        paymentRequest: PaymentRequest,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_PAYMENTS)
            .document()
            .set(paymentRequest)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun saveOrder(
        orderRequest: OrderRequest,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_ORDERS)
            .document()
            .set(orderRequest)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun updateTruck(
        truckId: String,
        updates: HashMap<String, Any>,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_TRUCKS)
            .document(truckId)
            .update(updates)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun updateOrder(
        orderId: String,
        updates: HashMap<String, Any>,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_ORDERS)
            .document(orderId)
            .update(updates)
            .addOnSuccessListener {
                successCallBack.invoke()
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getOrderByDay(
        date: String,
        truckId: String,
        successCallBack: (orders: List<Order?>) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_ORDERS)
            .whereEqualTo("date", date)
            .whereEqualTo("truckId", truckId)
            .get()
            .addOnSuccessListener { result ->
                val orders = mutableListOf<Order?>()
                result.documents.forEach {
                    val order = it.toObject(Order::class.java)
                    order?.id = it.id
                    orders.add(order)
                }

                successCallBack.invoke(orders)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getTruckById(
        truckId: String,
        successCallBack: (truck: Truck?) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_TRUCKS)
            .document(truckId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val id = document.id
                    val truck = document.toObject(Truck::class.java)!!
                    truck.id = id
                    successCallBack.invoke(truck)
                } else {
                    errorCallBack.invoke("Truck not found")
                }
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getCharges(
        clientId: String,
        successCallBack: (charges: List<Charge?>) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_CHARGES)
            .whereEqualTo("clientId", clientId)
            .whereEqualTo("finalized", false)
            .get()
            .addOnSuccessListener { result ->
                val charges = mutableListOf<Charge?>()
                result.documents.forEach {
                    val charge = it.toObject(Charge::class.java)
                    charge?.id = it.id
                    charges.add(charge)
                }

                successCallBack.invoke(charges)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun getPayments(
        successCallBack: (payment: List<Payment?>) -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.collection(DATABASE_PAYMENTS)
            .get()
            .addOnSuccessListener { result ->
                val payments = mutableListOf<Payment?>()
                result.documents.forEach {
                    val payment = it.toObject(Payment::class.java)
                    payment?.id = it.id
                    payments.add(payment)
                }

                successCallBack.invoke(payments)
            }
            .addOnFailureListener { exception ->
                errorCallBack.invoke(
                    exception.localizedMessage ?: exception.message ?: "Generic Error"
                )
                Log.d("Vendor", "Error", exception)
            }
    }

    fun updateCharges(
        charges: List<Charge?>,
        successCallBack: () -> Unit,
        errorCallBack: (error: String) -> Unit
    ) {
        db.runBatch { batch ->
            charges.forEach {
                val update =
                    db
                        .collection(DATABASE_CHARGES)
                        .document(it?.id!!)
                batch.update(update, "finalized", true)
            }
        }.addOnCompleteListener {
            successCallBack.invoke()
        }.addOnFailureListener { exception ->
            errorCallBack.invoke(
                exception.localizedMessage ?: exception.message ?: "Generic Error"
            )
            Log.d("Vendor", "Error", exception)
        }
    }
}