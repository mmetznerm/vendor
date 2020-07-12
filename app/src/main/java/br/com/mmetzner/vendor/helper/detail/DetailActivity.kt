package br.com.mmetzner.vendor.helper.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.helper.order.OrderViewModel
import br.com.mmetzner.vendor.model.*
import br.com.mmetzner.vendor.utils.Constants
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class DetailActivity : AppCompatActivity(), DetailAdapter.OnClickListener {

    private val viewModel: DetailViewModel by viewModel()
    private val paymentAdapter by lazy { PaymentAdapter(this) }
    private val chargeAdapter by lazy { DetailAdapter(arrayListOf(), this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.details)

        configureAdapters()
        configureObservers()
        configureButtons()

        setOrder()
        setView()
        updateFinalizeButtonText()

        getCharges()
        getPayments()
    }

    private fun setView() {
        val order = viewModel.mOrder.value
        tvClient.text = order?.client?.name
        tvAddress.text = order?.client?.address
        tvPhone.text = order?.client?.phone
    }

    private fun configureObservers() {
        viewModel.mCharges.observe(this, androidx.lifecycle.Observer {
            chargeAdapter.updateItems(it)
        })
        viewModel.mPayments.observe(this, androidx.lifecycle.Observer {
            paymentAdapter.addAll(it)
        })
        viewModel.allRequestsReady.observe(this, androidx.lifecycle.Observer {
            setResult(Constants.REQUEST_CODE_DETAIL_ACTIVITY)
            finish()
        })
    }

    private fun configureButtons() {
        btFinalize.setOnClickListener { checkOrder() }
    }

    private fun configureAdapters() {
        val layoutManager = LinearLayoutManager(this)
        rvChargeItems.layoutManager = layoutManager

        rvChargeItems.adapter = chargeAdapter
        spPayments.adapter = paymentAdapter
    }

    private fun setOrder() {
        viewModel.mOrder.value = Gson().fromJson(intent.getStringExtra(Constants.ORDER), Order::class.java)
    }

    private fun getCharges() {
        viewModel.getCharges()
    }

    private fun getPayments() {
        viewModel.getPayments()
    }

    private fun checkOrder() {
        viewModel.checkOrderBeforeFinalize(chargeAdapter.getItems(), paymentAdapter.getItem(spPayments.selectedItemPosition))
    }

    override fun onItemClicked(position: Int, charge: Charge?) {
        updateFinalizeButtonText()

    }

    private fun updateFinalizeButtonText() {
        btFinalize.text = String.format(getString(R.string.close), viewModel.getTotalOrder(chargeAdapter.getItems()))
    }


}
