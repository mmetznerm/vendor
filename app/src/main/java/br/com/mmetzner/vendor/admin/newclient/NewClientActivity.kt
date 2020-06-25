package br.com.mmetzner.vendor.admin.newclient

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.utils.CustomDialog.loadingDialog
import br.com.mmetzner.vendor.utils.CustomDialog.showError
import kotlinx.android.synthetic.main.activity_new_client.*
import org.koin.android.viewmodel.ext.android.viewModel

class NewClientActivity : AppCompatActivity() {

    private val viewModel: NewClientViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_client)
        title = getString(R.string.new_client)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configureButtons()
        configureObservers()
    }

    private fun configureObservers() {
        viewModel.loadingProgress.observe(this, Observer {
            loadingDialog(this, it)
        })
        viewModel.error.observe(this, Observer {
            showError(this, it)
        })
        viewModel.finishApp.observe(this, Observer {
            finish()
        })
    }

    private fun configureButtons() {
        btCreate.setOnClickListener { createClient() }
    }

    private fun createClient() {
        val name = tiltName.text.toString()
        val address = tiltAddress.text.toString()
        val cpfCnpj = tiltCpfCnpj.text.toString()
        val city = tiltCity.text.toString()
        val phone = tiltPhone.text.toString()

        val addressByGps = getLocationByGps(address)

        viewModel.saveClient(name, address, cpfCnpj, city, phone, addressByGps?.latitude, addressByGps?.longitude)
    }

    private fun getLocationByGps(address: String): Address? {
        val coder = Geocoder(this)
        val addresses = coder.getFromLocationName(address, 1)

        if(addresses.isNullOrEmpty()) {
            return null
        }

        return addresses[0]
    }
}
