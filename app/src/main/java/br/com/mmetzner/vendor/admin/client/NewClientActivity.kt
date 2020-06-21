package br.com.mmetzner.vendor.admin.client

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Client
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_client.*

class NewClientActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_client)
        title = getString(R.string.new_client)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        btCreate.setOnClickListener { createClient() }
    }

    private fun createClient() {
        val name = tiltName.text.toString()
        val address = tiltAddress.text.toString()
        val cpfCnpj = tiltCpfCnpj.text.toString()
        val cep = tiltCity.text.toString()
//        val cep = tiltPhone.text.toString()

        if(name.isEmpty() || address.isEmpty() || cpfCnpj.isEmpty() || cep.isEmpty()) {
            return
        }

        db
            .collection("clients")
            .document()
            .set(Client(name, address, cpfCnpj, cep))
            .addOnSuccessListener { result ->
                finish()

            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }
}
