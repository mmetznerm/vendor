package br.com.mmetzner.vendor.admin.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Truck
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_select_truck.*

class SelectTruckToNewOrder : AppCompatActivity(), SelectTruckAdapter.TruckListener {

    lateinit var truckSelected: Truck
    private lateinit var db: FirebaseFirestore
    private val mAdapter by lazy { SelectTruckAdapter(
        Gson().fromJson<List<Truck>>(intent?.getStringExtra("trucks"), object : TypeToken<List<Truck>>() {}.type), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_truck)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(this)
        rvTrucks.layoutManager = layoutManager

        rvTrucks.adapter = mAdapter
        rvTrucks.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onItemClick(truck: Truck) {
        this.truckSelected = truck
        val intent = Intent()
        intent.putExtra("truckSelected", Gson().toJson(this.truckSelected))
        setResult(444, intent)
        finish()
    }
}