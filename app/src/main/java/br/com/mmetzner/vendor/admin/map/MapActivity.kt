package br.com.mmetzner.vendor.admin.map

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.newclient.NewClientActivity
import br.com.mmetzner.vendor.admin.client.SelectClientActivity
import br.com.mmetzner.vendor.admin.newpayment.NewPaymentActivity
import br.com.mmetzner.vendor.admin.newproduct.NewProductActivity
import br.com.mmetzner.vendor.model.Truck
import br.com.mmetzner.vendor.utils.CustomDialog.loadingDialog
import br.com.mmetzner.vendor.utils.CustomDialog.showError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.koin.android.viewmodel.ext.android.viewModel

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val viewModel: MapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        configureMap()
        configureButtons()
        configureObservers()

        viewModel.getEnableTrucks()
    }

    private fun configureObservers() {
        viewModel.trucks.observe(this, Observer {
            addTrucksOnMap(it)
        })
        viewModel.truckDetailPopup.observe(this, Observer {
            showTruckDetailPopup(it.first, it.second)
        })
        viewModel.loadingProgress.observe(this, Observer {
            loadingDialog(this, it)
        })
        viewModel.error.observe(this, Observer {
            showError(this, it)
        })
    }

    private fun showTruckDetailPopup(licensePlate: String?, productsMessage: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(licensePlate)
        dialog.setMessage(productsMessage)
        dialog.show()
    }

    private fun configureButtons() {
        btNewOrder.setOnClickListener { openClientActivity() }
    }

    private fun configureMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    private fun addTrucksOnMap(trucks: List<Truck?>) {
        val builder = LatLngBounds.Builder()

        trucks.forEach {
            val location = LatLng(it!!.latitude, it.longitude)
            val marker = MarkerOptions().position(location)
            mMap.addMarker(marker.title(it.licensePlate))
            mMap.setOnMarkerClickListener { p0 ->
                viewModel.getProductsDetailByTruck(p0.title)
                true
            }
            builder.include(marker.position)
        }
        val bounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
    }
}
