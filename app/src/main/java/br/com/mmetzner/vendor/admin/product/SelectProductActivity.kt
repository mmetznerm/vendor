package br.com.mmetzner.vendor.admin.product

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_select_product.*
import java.text.DecimalFormat
import java.util.*

class SelectProductActivity : AppCompatActivity() {

    private lateinit var sendOrderFunction: () -> Unit
    private lateinit var truckSelected: Truck
    private lateinit var trucks: MutableList<Truck>
    private lateinit var db: FirebaseFirestore
    private val mAdapter by lazy { SelectProductAdapter(arrayListOf()) }
    private val mClient by lazy { Gson().fromJson(intent.getStringExtra("client"), Client::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager

        rvRecyclerView.adapter = mAdapter
        rvRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        btFinalize.setOnClickListener { checkOrder() }
        tvDate.setOnClickListener { openCalendar() }

        tvClient.text = mClient.name
        tvAddress.text = mClient.address
        tvPhone.text = mClient.phone

        setDate(Calendar.getInstance())

        getTrucks()
        getProducts()
    }

    private fun setDate(calendar: Calendar) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        tvDate.text = "$day/${month + 1}/$year"
    }

    private fun checkOrder() {
        val clientId = mClient.id
        val products = mAdapter.getItems()
            .filter { it.quantity > 0 }
            .map { ProductItemRequest(it.id, it.quantity) }
        val date = tvDate.text.toString()

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Pergunta")
        dialog.setMessage("Deseja entregar o pedido a um caminhao especifico ou que o sistema calcule o mais próximo?")
        dialog.setNegativeButton("Caminhão"
        ) { _, _ -> switchTruckBeforeSendOrder() }
        dialog.setPositiveButton("Calcular"
        ) { _, _ -> calculateTruckBeforeSendOrder() }
        dialog.show()

        sendOrderFunction = { sendOrder(clientId, products, date) }
    }

    fun calculateDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1: Double = StartP.latitude
        val lat2: Double = EndP.latitude
        val lon1: Double = StartP.longitude
        val lon2: Double = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return Radius * c
    }

    private fun openCalendar() {
        val myCalendar = Calendar.getInstance()
        DatePickerDialog(
            this@SelectProductActivity,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                setDate(myCalendar)
            }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getTrucks() {
        db.collection("trucks")
            .get()
            .addOnSuccessListener { result ->
                val trucks = mutableListOf<Truck>()

                val documents = result.documents
                for(document in documents) {
                    val id = document.id
                    val truck = document.toObject(Truck::class.java)!!
                    truck.id = id
                    trucks.add(truck)
                }
                this.trucks = trucks
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun calculateTruckBeforeSendOrder() {
        var truckNearAddress: Pair<Double, Truck>? = null

        trucks.forEach {
            val distance = calculateDistance(LatLng(mClient.latitude, mClient.longitude), LatLng(it.latitude, it.longitude))
            if(truckNearAddress == null) {
                truckNearAddress = Pair(distance, it)
            } else {
                if(truckNearAddress!!.first > distance ) {
                    truckNearAddress = Pair(distance, it)
                }
            }
        }

        this.truckSelected = truckNearAddress!!.second

        Toast.makeText(this, "Enviado para o veículo: ${truckSelected.licensePlate}", Toast.LENGTH_SHORT).show()

        sendOrderFunction.invoke()
    }

    private fun switchTruckBeforeSendOrder() {
        val intent = Intent(this, SelectTruckToNewOrder::class.java)
        intent.putExtra("trucks", Gson().toJson(this.trucks))
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        this.truckSelected = Gson().fromJson(data?.getStringExtra("truckSelected"), Truck::class.java)
        sendOrderFunction.invoke()
    }

    private fun sendOrder(clientId: String?, products: List<ProductItemRequest>, date: String?) {
        db
            .collection("orders")
            .document()
            .set(OrderRequest(clientId!!, products, date!!, truckSelected.id!!))
            .addOnSuccessListener {
                finish()

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun getProducts() {
        db
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = mutableListOf<Product>()

                val documents = result.documents
                for(document in documents) {
                    val id = document.id
                    val product = document.toObject(Product::class.java)!!
                    product.id = id
                    products.add(product)

                    mAdapter.updateItems(products.sortedBy { it.description })
                }

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }
}
