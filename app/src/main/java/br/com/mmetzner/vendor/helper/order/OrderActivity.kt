package br.com.mmetzner.vendor.helper.order

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.utils.CustomDate
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.newclient.NewClientActivity
import br.com.mmetzner.vendor.helper.detail.DetailActivity
import br.com.mmetzner.vendor.helper.route.StartRouteActivity
import br.com.mmetzner.vendor.model.*
import br.com.mmetzner.vendor.utils.Constants
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.android.synthetic.main.activity_orders.rvRecyclerView
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class OrderActivity : AppCompatActivity(),
    OrderAdapter.OnClickListener {

    private val mAdapter by lazy {
        OrderAdapter(
            arrayListOf(),
            this
        )
    }
    private val viewModel: OrderViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        title = getString(R.string.route_of_day)

        configureList()
        configureObservers()

        setUser()
    }

    override fun onResume() {
        super.onResume()
        getOrders()
        getProducts()
        getClients()
        getTruck()
    }

    private fun setUser() {
        viewModel.mUser.value = Gson().fromJson(intent.getStringExtra(Constants.USER), User::class.java)
    }

    private fun getClients() {
        viewModel.getClients()
    }

    private fun getProducts() {
        viewModel.getProducts()
    }

    private fun getOrders() {
        viewModel.getOrderByDay(CustomDate.getCurrentDateFormated())
    }

    private fun configureObservers() {
        viewModel.mTruck.observe(this, androidx.lifecycle.Observer {
            changeButtonFunction(it)
        })
        viewModel.allRequestsReady.observe(this, androidx.lifecycle.Observer {
            mAdapter.updateItems(viewModel.mOrders.value?.toList())
        })
    }

    private fun configureList() {
        val layoutManager = LinearLayoutManager(this)
        rvRecyclerView.layoutManager = layoutManager
        rvRecyclerView.adapter = mAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_order, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.calendar -> {
                openCalendar()
                true
            }
            R.id.menuNewClient -> {
                startActivity(Intent(this, NewClientActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openCalendar() {
        val myCalendar = CustomDate.currentDate
        DatePickerDialog(
            this@OrderActivity,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                CustomDate.currentDate = myCalendar
                getOrders()
                getProducts()
                getClients()
            }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.REQUEST_CODE_ROUTE_ACTIVITY) {
            getTruck()
        }
        if(requestCode == Constants.REQUEST_CODE_DETAIL_ACTIVITY) {
            getOrders()
            getProducts()
            getClients()
        }
    }

    private fun closeRoute() {
        viewModel.updateTruck()
    }

    private fun openRouteActivity() {
        val sugestedQuantities = viewModel.getSuggestedQuantities(mAdapter.getItems())

        val intent = Intent(this, StartRouteActivity::class.java)
        intent.putExtra(Constants.TRUCK_ID, viewModel.mUser.value?.truckId)
        intent.putExtra(Constants.SUGGESTED_PRODUCTS, Gson().toJson(sugestedQuantities))
        startActivityForResult(intent, Constants.REQUEST_CODE_ROUTE_ACTIVITY)
    }

    private fun getTruck() {
        viewModel.getTruck()
    }

    private fun changeButtonFunction(truck: Truck) {
        if(truck.routeStarted) {
            btStartRoute.text = getString(R.string.close_route)
            btStartRoute.background = ContextCompat.getDrawable(this, R.drawable.button_background_green)
            btStartRoute.setOnClickListener { closeRoute() }
        } else {
            btStartRoute.text = getString(R.string.init_route)
            btStartRoute.background = ContextCompat.getDrawable(this, R.drawable.button_background_blue)
            btStartRoute.setOnClickListener { openRouteActivity() }
        }
    }

    override fun onItemClicked(position: Int, order: Order?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(Constants.ORDER, Gson().toJson(order))
        startActivityForResult(intent, Constants.REQUEST_CODE_DETAIL_ACTIVITY)
    }

}
