package br.com.mmetzner.vendor.admin.newpayment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.utils.CustomDialog
import kotlinx.android.synthetic.main.activity_new_payment.*
import org.koin.android.viewmodel.ext.android.viewModel

class NewPaymentActivity : AppCompatActivity() {

    private val viewModel: NewPaymentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_payment)
        title = getString(R.string.new_payment)
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
        btCreate.setOnClickListener { createPayment() }
    }

    private fun createPayment() {
        val description = tiltDescription.text.toString()
        val days = tiltDays.text.toString()
        val isCharge = sIsCharge.isChecked

        viewModel.savePayment(description, days.toInt(), isCharge)
    }
}
