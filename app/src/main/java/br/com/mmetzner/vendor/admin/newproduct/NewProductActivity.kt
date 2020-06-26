package br.com.mmetzner.vendor.admin.newproduct

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.utils.CustomDialog
import kotlinx.android.synthetic.main.activity_new_product.*
import org.koin.android.viewmodel.ext.android.viewModel

class NewProductActivity : AppCompatActivity() {

    private val viewModel: NewProductViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)
        title = getString(R.string.new_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configureButtons()
        configureObservers()
    }

    private fun configureObservers() {
        viewModel.loadingProgress.observe(this, Observer {
            CustomDialog.loadingDialog(this, it)
        })
        viewModel.error.observe(this, Observer {
            CustomDialog.showError(this, it)
        })
        viewModel.finishApp.observe(this, Observer {
            finish()
        })
    }

    private fun configureButtons() {
        btCreate.setOnClickListener { createProduct() }
    }

    private fun createProduct() {
        val description = tiltDescription.text.toString()
        val price = tiltPrice.text.toString()

        viewModel.saveProduct(description, price.toDouble())
    }
}
