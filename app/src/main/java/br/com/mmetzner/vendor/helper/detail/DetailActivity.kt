package br.com.mmetzner.vendor.helper.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

class DetailActivity : AppCompatActivity(), DetailAdapter.OnClickListener {

    private val mOrder by lazy { Gson().fromJson(intent.getStringExtra("order"), Order::class.java) }
    private val paymentAdapter by lazy { PaymentAdapter(this) }

    private val mAdapter by lazy {
        DetailAdapter(
            arrayListOf(),
            this
        )
    }
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.details)

        db = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(this)
        rvChargeItems.layoutManager = layoutManager

        rvChargeItems.adapter = mAdapter
        spinner3.adapter = paymentAdapter

        btFinalize.setOnClickListener { checkOrder() }
        tvClient.text = mOrder.client?.name
        tvAddress.text = mOrder.client?.address
        tvPhone.text = mOrder.client?.phone

        val totalProducts = calculateTotalProducts()
        //textView2.text = "Produtos: ${totalProducts}"
        btFinalize.text = "Fechar: R$ $totalProducts"

        getCharges(mOrder?.clientId)
        getPayments()
    }

    private fun getCharges(clientId: String?) {
        db.collection("charges")
            .whereEqualTo("clientId", clientId)
            .whereEqualTo("finalized", false)
            .get()
            .addOnSuccessListener { result ->
                val charges = mutableListOf<Charge>()

                val documents = result.documents
                for(document in documents) {
                    val id = document.id
                    val charge = document.toObject(Charge::class.java)!!
                    charge.id = id
                    charges.add(charge)
                }
                mAdapter.updateItems(charges)
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun getPayments() {
        db.collection("payments")
            .get()
            .addOnSuccessListener { result ->
                val payments = mutableListOf<Payment>()

                val documents = result.documents
                for(document in documents) {
                    val id = document.id
                    val payment = document.toObject(Payment::class.java)!!
                    payment.id = id
                    payments.add(payment)
                }
                paymentAdapter.addAll(payments.toTypedArray())
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun checkOrder() {
        val orderId = mOrder.id
        val clientId = mOrder.clientId
        val total = calculateTotalProducts()?.plus(calculateTotalCharges())

        finalizeCharges(clientId, orderId, mAdapter.getItems().filter { it.selected })
        finalizeOrder(clientId, orderId, total)
    }

    private fun finalizeCharges(clientId: String?, orderId: String?, charges: List<Charge>) {
        if(charges.isEmpty()) {
            return
        }
        db
            .runBatch { batch ->
                charges.forEach {
                    val sff = db
                        .collection("charges")
                        .document(it.id!!)
                    batch.update(sff, "finalized", true)
                }
            }.addOnCompleteListener {
                Toast.makeText(this, "Feito", Toast.LENGTH_SHORT).show()
            }
    }

    private fun finalizeOrder(clientId: String?, orderId: String?, total: Double?) {
        val payment = paymentAdapter.getItem(spinner3.selectedItemPosition)
        val date = mountDate(Calendar.getInstance(), payment.days)

        if(payment.charge) {
            db
                .collection("charges")
                .document()
                .set(ChargeRequest(clientId, orderId, total, date))
                .addOnSuccessListener { result ->
                    updateOrder(orderId)

                }
                .addOnFailureListener { exception ->
                    Log.d("VENDOR", "Error getting documents: ", exception)
                }
        } else {
            updateOrder(orderId)
        }

    }

    private fun mountDate(calendar: Calendar, days: Int): String? {
        calendar.add(Calendar.DAY_OF_MONTH, days)

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        return "$day/${month + 1}/$year"
    }

    private fun calculateTotalProducts(): Double? {
        return mOrder.productList?.map { it!!.quantity * it.price }?.sum()
    }

    private fun calculateTotalCharges(): Double {
        return mAdapter.getItems().filter { it.selected }.sumByDouble { it.value }
    }

    private fun updateOrder(orderId: String?) {
        val updates = hashMapOf<String, Any>(
            "finished" to true
        )

        db
            .collection("orders")
            .document(orderId!!)
            .update(updates)
            .addOnSuccessListener { result ->
                setResult(344)
                finish()
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    override fun onItemClicked(position: Int, charge: Charge) {
        btFinalize.text = "Fechar: R$ ${calculateTotalProducts()?.plus(calculateTotalCharges())}"
    }


}
