package br.com.mmetzner.vendor.admin.payment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.PaymentRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_payment.*

class NewPaymentActivity : AppCompatActivity(){

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_payment)
        title = getString(R.string.new_payment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        btCreate.setOnClickListener { createPayment() }
    }

    private fun createPayment() {
        val description = tiltDescription.text.toString()
        if (description.isEmpty()) {
            return
        }

        val isCharge = sIsCharge.isChecked

        db
            .collection("payments")
            .document()
            .set(PaymentRequest(description, isCharge))
            .addOnSuccessListener { result ->
                finish()

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }
}
