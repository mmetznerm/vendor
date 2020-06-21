package br.com.mmetzner.vendor.admin.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.admin.product.SelectProductActivity
import br.com.mmetzner.vendor.model.Client
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_select_client.*

class SelectClientActivity : AppCompatActivity(), SelectClientAdapter.OnClickListener {

    private lateinit var db: FirebaseFirestore
    private val mAdapter by lazy { SelectClientAdapter(arrayListOf(), this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_client)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.search_client)

        db = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(this)
        rvClients.layoutManager = layoutManager
        rvClients.adapter = mAdapter

        getClients()
    }

    private fun getClients() {
        db
            .collection("clients")
            .get()
            .addOnSuccessListener { result ->
                val clients = mutableListOf<Client>()

                val documents = result.documents
                for(document in documents) {
                    val id = document.id
                    val client = document.toObject(Client::class.java)!!
                    client.id = id
                    clients.add(client)

                    mAdapter.updateItems(clients)
                }

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    override fun onItemClicked(position: Int, client: Client) {
        val intent = Intent(this, SelectProductActivity::class.java)
        intent.putExtra("client", Gson().toJson(client))
        startActivity(intent)
    }
}
