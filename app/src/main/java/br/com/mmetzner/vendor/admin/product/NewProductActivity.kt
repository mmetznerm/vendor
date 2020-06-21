package br.com.mmetzner.vendor.admin.product

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.ProductRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_product.*


class NewProductActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)
        title = getString(R.string.new_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        btCreate.setOnClickListener { createProduct() }
    }

    private fun createProduct() {
        val description = tiltDescription.text.toString()
        val price = tiltPrice.text.toString()
        if (description.isEmpty() || price.isEmpty()) {
            return
        }

        db
            .collection("products")
            .document()
            .set(ProductRequest(description, price.toDouble()))
            .addOnSuccessListener { result ->
                finish()

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }
}
