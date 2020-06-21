package br.com.mmetzner.vendor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.mmetzner.vendor.admin.MapActivity
import br.com.mmetzner.vendor.helper.OrderActivity
import br.com.mmetzner.vendor.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()
        configureLoginButton()
    }

    private fun configureLoginButton() {
        btLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userNameEmpty = tiltEmail.text.toString().isEmpty()
                val userPasswordEmpty = tiltPassword.text.toString().isEmpty()

                btLogin.isEnabled = !(userNameEmpty || userPasswordEmpty)
            }
        })
        btLogin.setOnClickListener {
            val userName = tiltEmail.text.toString()
            val userPassword = tiltPassword.text.toString()
            login(userName, userPassword)
        }
    }

    private fun login(email: String, password: String) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = mutableListOf<User?>()

                val documents = result.documents
                for(document in documents) {
                    users.add(document.toObject(User::class.java))
                }

                checkIfUserExists(users, email, password)
            }
            .addOnFailureListener { exception ->
                Log.d("VENDOR", "Error getting documents: ", exception)
            }
    }

    private fun checkIfUserExists(users: MutableList<User?>, email: String, password: String) {
        val user = users.firstOrNull { it?.email == email && it.password == password }

        if(user != null) {
            if(user.type == 0) {
                goToAdminTask(user)
            } else {
                goToHelperTask(user)
            }
        } else {
            Toast.makeText(this, "Email ou Senha inválidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToAdminTask(user: User) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("user", Gson().toJson(user))
        startActivity(intent)
    }

    private fun goToHelperTask(user: User) {
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("user", Gson().toJson(user))
        startActivity(intent)
    }
}
