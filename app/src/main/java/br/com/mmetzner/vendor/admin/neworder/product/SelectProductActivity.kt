package br.com.mmetzner.vendor.admin.neworder.product

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.product.SelectTruckToNewOrder
import br.com.mmetzner.vendor.model.Client
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.model.Truck
import br.com.mmetzner.vendor.utils.Constants
import br.com.mmetzner.vendor.utils.CustomDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_select_product.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class SelectProductActivity : AppCompatActivity() {

    private val viewModel: SelectProductViewModel by viewModel()
    private val mClient by lazy { Gson().fromJson(intent.getStringExtra(Constants.CLIENT), Client::class.java) }
    private val mTrucks by lazy { Gson().fromJson<List<Truck>>(intent?.getStringExtra(Constants.TRUCKS), object : TypeToken<List<Truck>>() {}.type) }
    private val mProducts by lazy { Gson().fromJson<List<Product>>(intent?.getStringExtra(Constants.PRODUCTS), object : TypeToken<List<Product>>() {}.type) }
    private var mAdapter: SelectProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.insert_products)

        configureList()
        configureButtons()
        configureObservers()

        setViewModelInfos(mClient, mTrucks)

        showInfos()
    }

    private fun configureObservers() {
        viewModel.mTruckSelected.observe(this, androidx.lifecycle.Observer { truckSelected ->
            Toast.makeText(this, getString(R.string.message_truck_delivered, truckSelected.licensePlate), Toast.LENGTH_SHORT).show()
            viewModel.sendOrder(mClient, truckSelected, mAdapter?.getItems(), tvDate.text.toString())
        })
        viewModel.finishApp.observe(this, androidx.lifecycle.Observer {
            finish()
        })
    }

    private fun setViewModelInfos(mClient: Client?, mTrucks: List<Truck>?) {
        viewModel.mClient.value = mClient
        viewModel.mTrucks.value = mTrucks
    }

    private fun showInfos() {
        tvClient.text = mClient.name
        tvAddress.text = mClient.address
        tvPhone.text = mClient.phone
        tvDate.text = CustomDate.formatDateToString(Calendar.getInstance())
    }

    private fun configureButtons() {
        btFinalize.setOnClickListener { showQuestionBeforeSendOrder() }
        tvDate.setOnClickListener { openCalendar() }
    }

    private fun configureList() {
        mAdapter = SelectProductAdapter(mProducts.sortedBy { it.description })
        val layoutManager = LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager
        rvRecyclerView.adapter = mAdapter
        rvRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun showQuestionBeforeSendOrder() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.attention))
        dialog.setMessage(getString(R.string.message_before_send_order))
        dialog.setNegativeButton(getString(R.string.truck)
        ) { _, _ -> switchTruckBeforeSendOrder() }
        dialog.setPositiveButton(getString(R.string.calculate)
        ) { _, _ -> calculateTruckBeforeSendOrder() }
        dialog.show()
    }

    private fun calculateTruckBeforeSendOrder() {
        viewModel.calculateTruckBeforeSendOrder()
    }

    private fun openCalendar() {
        val myCalendar = Calendar.getInstance()
        DatePickerDialog(
            this@SelectProductActivity,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                tvDate.text = CustomDate.formatDateToString(myCalendar)
            }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun switchTruckBeforeSendOrder() {
        val intent = Intent(this, SelectTruckToNewOrder::class.java)
        intent.putExtra(Constants.TRUCKS, Gson().toJson(mTrucks))
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val truckSelected = Gson().fromJson(data?.getStringExtra(Constants.TRUCK_SELECTED), Truck::class.java)
        viewModel.mTruckSelected.postValue(truckSelected)
    }
}
