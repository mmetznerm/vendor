package br.com.mmetzner.vendor.helper.route

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.model.ProductItemRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_select_product.*


class StartRouteActivity : AppCompatActivity() {

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private val PERMISSION_ID: Int = 345
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var db: FirebaseFirestore
    private val mAdapter by lazy { StartRouteAdapter(arrayListOf()) }
    private val mTruckId by lazy { intent.getStringExtra("truckId") }
    private val mSugestedQuantities by lazy { Gson().fromJson<List<Product>>(intent.getStringExtra("sugestedQuantities"), object : TypeToken<List<Product>>() {}.type) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)
        title = getString(R.string.load_sugestion)

        db = FirebaseFirestore.getInstance()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val layoutManager = LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager

        rvRecyclerView.adapter = mAdapter
        rvRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        btFinalize.setOnClickListener { checkProducts() }

        getLastLocation()
        getProducts()
    }

    private fun checkProducts() {
        val products = mAdapter.getItems()
            .map { ProductItemRequest(it.id, it.quantity) }

        startRoute(mTruckId, products)
    }

    private fun startRoute(
        truckId: String,
        products: List<ProductItemRequest>
    ) {
        val updates = hashMapOf(
            "routeStarted" to true,
            "products" to products,
            "latitude" to this.currentLatitude,
            "longitude" to this.currentLongitude
        )

        db
            .collection("trucks")
            .document(truckId)
            .update(updates)
            .addOnSuccessListener {
                setResult(188)
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
                for (document in documents) {
                    val id = document.id
                    val product = document.toObject(Product::class.java)!!
                    product.id = id
                    products.add(product)

                }
                mSugestedQuantities.forEach {
                    val sugestedProduct = products.first { product -> product.description.equals(it.description) }
                    sugestedProduct.quantity = it.quantity
                }

                mAdapter.updateItems(products.sortedBy { it.description })

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this@StartRouteActivity, "Coordenadas não encontradas", Toast.LENGTH_SHORT).show()
                    } else {
                        this.currentLatitude = location.latitude
                        this.currentLongitude = location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}
