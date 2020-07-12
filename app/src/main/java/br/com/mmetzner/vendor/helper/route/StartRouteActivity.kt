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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_select_product.*
import org.koin.android.viewmodel.ext.android.viewModel


class StartRouteActivity : AppCompatActivity() {

    private val viewModel: StartRouteViewModel by viewModel()
    private val mAdapter by lazy { StartRouteAdapter(arrayListOf()) }
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_route)
        title = getString(R.string.load_sugestion)

        setTruckId()
        setProducts()

        mountSuggestedList()

        configureList()
        configureObservers()
        configureButtons()
        configureLocationService()
    }

    private fun setProducts() {
        viewModel.mProducts.value = Gson().fromJson<List<Product>>(intent.getStringExtra(Constants.PRODUCTS), object : TypeToken<List<Product>>() {}.type)
    }

    private fun mountSuggestedList() {
        val suggestedQuantities = Gson().fromJson<List<Product>>(intent.getStringExtra(Constants.SUGGESTED_QUANTITIES), object : TypeToken<List<Product>>() {}.type)
        viewModel.mountSuggestedList(suggestedQuantities)
    }

    private fun setTruckId() {
        viewModel.mTruckId.value = intent.getStringExtra(Constants.TRUCK_ID)
    }

    private fun configureLocationService() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun configureButtons() {
        btFinalize.setOnClickListener { getLastLocation() }
    }

    private fun configureObservers() {
        viewModel.allRequestsReady.observe(this, Observer {
            setResult(Constants.REQUEST_CODE_ROUTE_ACTIVITY)
            finish()
        })
        viewModel.mSuggestedProducts.observe(this, Observer { product ->
            mAdapter.updateItems(product.sortedBy { it?.description })
        })
    }

    private fun configureList() {
        val layoutManager = LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager

        rvRecyclerView.adapter = mAdapter
        rvRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun startRoute(latitude: Double, longitude: Double) {
        viewModel.startRoute(mAdapter.getItems(), latitude, longitude)
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
            Constants.REQUEST_CODE_PERMISSIONS
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
                        Toast.makeText(this@StartRouteActivity, getString(R.string.coordinates_not_found), Toast.LENGTH_SHORT).show()
                    } else {
                        startRoute(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.turn_on_gps), Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}
