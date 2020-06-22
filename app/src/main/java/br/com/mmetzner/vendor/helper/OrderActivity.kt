package br.com.mmetzner.vendor.helper

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.utils.Date
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.client.NewClientActivity
import br.com.mmetzner.vendor.helper.detail.DetailActivity
import br.com.mmetzner.vendor.helper.route.StartRouteActivity
import br.com.mmetzner.vendor.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.android.synthetic.main.activity_orders.rvRecyclerView
import java.util.*


class OrderActivity : AppCompatActivity(), OrderAdapter.OnClickListener {

    private val mAdapter by lazy { OrderAdapter(arrayListOf(), this) }
    private val mUser by lazy { Gson().fromJson(intent.getStringExtra("user"), User::class.java) }
    private var mTruck: Truck? = null

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        title = getString(R.string.route_of_day)

        db = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager

        rvRecyclerView.adapter = mAdapter

        btStartRoute.setOnClickListener { checkButtonFunction(mTruck?.routeStarted) }

        getOrdersList()
        getTruckInformation(mUser.truckId!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_order, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.calendar -> {
                openCalendar()
                true
            }
            R.id.menuNewClient -> {
                startActivity(Intent(this, NewClientActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openCalendar() {
        val myCalendar = Date.currentDate
        DatePickerDialog(
            this@OrderActivity,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                Date.currentDate = myCalendar
                getOrdersList()
            }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 188) {
            getTruckInformation(mUser.truckId!!)
        }
        if(requestCode == 344) {
            getOrdersList()
        }
    }

    private fun checkButtonFunction(routeStarted: Boolean?) {
        if(routeStarted == true) {
            closeRoute()
        } else {
            openRouteActivity()
        }
    }

    private fun closeRoute() {
        val updates = hashMapOf<String, Any>(
            "routeStarted" to false,
            "products" to arrayListOf<Product>(),
            "latitude" to 0.0,
            "longitude" to 0.0
        )

        db
            .collection("trucks")
            .document(mTruck?.id!!)
            .update(updates)
            .addOnSuccessListener {
                getTruckInformation(mUser.truckId!!)
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun openRouteActivity() {
        val sugestedQuantities = getSugestedQuantities()
        val intent = Intent(this, StartRouteActivity::class.java)
        intent.putExtra("truckId", mUser.truckId)
        intent.putExtra("sugestedQuantities", Gson().toJson(sugestedQuantities))
        startActivityForResult(intent, 188)
    }

    private fun getSugestedQuantities(): List<Product> {
        val products = mutableListOf<Product>()
        mAdapter.getItems().forEach { it ->
            it.productList?.groupBy { it.description }?.forEach { product ->
                products.add(
                    Product(
                        description = product.key,
                        quantity = product.value.sumBy { it.quantity })
                )
            }
        }
        return products.groupBy { it.description }
            .map { Product(description = it.key, quantity = it.value.sumBy { it.quantity }) }
    }

    private fun getTruckInformation(truckId: String) {
        db
            .collection("trucks")
            .document(truckId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val id = document.id
                    val truck = document.toObject(Truck::class.java)!!
                    truck.id = id
                    this.mTruck = truck

                    setButtonClick()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun setButtonClick() {
        if(mTruck?.routeStarted!!) {
            btStartRoute.text = "Encerrar Rota"
            btStartRoute.background = ContextCompat.getDrawable(this, R.drawable.button_background_green)
        } else {
            btStartRoute.text = "Iniciar Rota"
            btStartRoute.background = ContextCompat.getDrawable(this, R.drawable.button_background_blue)
        }
    }

    private fun getOrdersList() {
        val date = convertDate(Date.currentDate)
        db.collection("orders")
            .whereEqualTo("date", date)
            .whereEqualTo("truckId", mUser.truckId)
            .get()
            .addOnSuccessListener { result ->
                val orders = mutableListOf<Order>()

                val documents = result.documents
                for (document in documents) {
                    val id = document.id
                    val order = document.toObject(Order::class.java)!!
                    order.id = id
                    orders.add(order)
                }

                db.collection("clients")
                    .get()
                    .addOnSuccessListener { result1 ->
                        val clients = mutableListOf<Client>()

                        val documents1 = result1.documents
                        for (document1 in documents1) {
                            val id = document1.id
                            val client = document1.toObject(Client::class.java)!!
                            client.id = id
                            clients.add(client)
                        }
                        db.collection("products")
                            .get()
                            .addOnSuccessListener { result2 ->
                                val products = mutableListOf<Product>()

                                val documents2 = result2.documents
                                for (document2 in documents2) {
                                    val id = document2.id
                                    val product = document2.toObject(Product::class.java)!!
                                    product.id = id
                                    products.add(product)
                                }

                                mountOrders(orders, clients, products)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("VENDOR", "Error getting documents: ", exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("VENDOR", "Error getting documents: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun convertDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        return "$day/${month + 1}/$year"
    }

    private fun mountOrders(
        orders: MutableList<Order>,
        clients: MutableList<Client>,
        products: MutableList<Product>
    ) {
        val newProducts: MutableList<Product> = mutableListOf()

        orders.forEach { order ->
            val productsByOrder = order.products
            productsByOrder?.forEach { p ->
                val product = products.first { it.id == p.productId }
                product.quantity = p.productQuantity
                newProducts.add(product)
            }
            order.client = clients.first { it.id == order.clientId }
            order.productList = newProducts
        }

        mAdapter.updateItems(orders)
    }

    override fun onItemClicked(position: Int, order: Order) {
        val gson = Gson()
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("order", gson.toJson(order))
        startActivityForResult(intent, 344)
    }

}
