package br.com.mmetzner.vendor.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.client.NewClientActivity
import br.com.mmetzner.vendor.admin.client.SelectClientActivity
import br.com.mmetzner.vendor.admin.payment.NewPaymentActivity
import br.com.mmetzner.vendor.admin.product.NewProductActivity
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.model.Truck
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        db = FirebaseFirestore.getInstance()

        btNewOrder.setOnClickListener { openClientActivity() }

        getTrucksLocation()
    }

    private fun openClientActivity() {
        startActivity(Intent(this, SelectClientActivity::class.java))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menuNewClient -> {
                startActivity(Intent(this, NewClientActivity::class.java))
                true
            }
            R.id.menuNewProduct -> {
                startActivity(Intent(this, NewProductActivity::class.java))
                true
            }
            R.id.menuNewPayment -> {
                startActivity(Intent(this, NewPaymentActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getTrucksLocation() {
        db.collection("trucks")
            .whereEqualTo("routeStarted", true)
            .get()
            .addOnSuccessListener { result ->
                val trucks = mutableListOf<Truck?>()
                val builder = LatLngBounds.Builder()

                val documents = result.documents
                for(document in documents) {
                    trucks.add(document.toObject(Truck::class.java))
                }

                if(trucks.isNotEmpty()) {
                    trucks.forEach {
                        val location = LatLng(it!!.latitude, it.longitude)
                        val marker = MarkerOptions().position(location)
                        mMap.addMarker(marker.title(it.licensePlate))
                        mMap.setOnMarkerClickListener { p0 ->
                            getTruckDetails(p0?.title)
                            true
                        }
                        builder.include(marker.position)
                    }
                    val bounds = builder.build()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
                }

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun getTruckDetails(licensePlate: String?) {
        db.collection("trucks")
            .whereEqualTo("licensePlate", licensePlate)
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
                getProductDetails(trucks.first())
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun getProductDetails(truck: Truck) {
        val truckProducts = truck.products
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = mutableListOf<Product>()

                val documents = result.documents
                for(document in documents) {
                    val id = document.id
                    val product = document.toObject(Product::class.java)!!
                    product.id = id
                    products.add(product)
                }

                truckProducts?.forEach { truckProduct ->
                    val product = products.first { it.id == truckProduct.productId }
                    truckProduct.productDescription = product.description
                }

                val message = StringBuilder()
                truckProducts?.forEach {
                    message.append("${it.productDescription} - ${it.productQuantity}\n")
                }

                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(truck.licensePlate)
                dialog.setMessage(message.toString())
                dialog.show()

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }
}
