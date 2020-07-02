package br.com.mmetzner.vendor.admin.neworder.client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.neworder.product.SelectProductActivity
import br.com.mmetzner.vendor.model.Client
import br.com.mmetzner.vendor.model.Product
import br.com.mmetzner.vendor.model.Truck
import br.com.mmetzner.vendor.utils.Constants
import br.com.mmetzner.vendor.utils.CustomDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_select_client.*
import org.koin.android.viewmodel.ext.android.viewModel

class SelectClientActivity : AppCompatActivity(), SelectClientAdapter.OnClickListener {

    private val viewModel: SelectClientViewModel by viewModel()
    private val mAdapter by lazy { SelectClientAdapter(arrayListOf(), this) }
    private val mTrucks by lazy { Gson().fromJson<List<Truck>>(intent?.getStringExtra(Constants.TRUCKS), object : TypeToken<List<Truck>>() {}.type) }
    private val mProducts by lazy { Gson().fromJson<List<Product>>(intent?.getStringExtra(Constants.PRODUCTS), object : TypeToken<List<Product>>() {}.type) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_client)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.search_client)

        configureList()
        configureObservers()

        viewModel.getClients()
    }

    private fun configureObservers() {
        viewModel.loadingProgress.observe(this, Observer {
            CustomDialog.loadingDialog(this, it)
        })
        viewModel.error.observe(this, Observer {
            CustomDialog.showError(this, it)
        })
        viewModel.clients.observe(this, Observer {
            mAdapter.updateItems(it)
        })
    }

    private fun configureList() {
        val layoutManager = LinearLayoutManager(this)
        rvClients.layoutManager = layoutManager
        rvClients.adapter = mAdapter
    }

    override fun onItemClicked(position: Int, client: Client?) {
        val intent = Intent(this, SelectProductActivity::class.java)
        val gson = Gson()
        intent.putExtra(Constants.CLIENT, gson.toJson(client))
        intent.putExtra(Constants.TRUCKS, gson.toJson(mTrucks))
        intent.putExtra(Constants.PRODUCTS, gson.toJson(mProducts))
        startActivity(intent)
    }
}
