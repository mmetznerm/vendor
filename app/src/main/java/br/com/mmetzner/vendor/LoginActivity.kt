package br.com.mmetzner.vendor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import br.com.mmetzner.vendor.admin.MapActivity
import br.com.mmetzner.vendor.helper.OrderActivity
import br.com.mmetzner.vendor.model.User
import br.com.mmetzner.vendor.utils.Constants
import br.com.mmetzner.vendor.utils.Dialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        configureObservers()
        configureLoginButton()
    }

    private fun configureObservers(){
        viewModel.adminTask.observe(this, Observer {
            goToNextStep(it, MapActivity::class.java)
        })
        viewModel.helperTask.observe(this, Observer {
            goToNextStep(it, OrderActivity::class.java)
        })
        viewModel.error.observe(this, Observer {
            showError(it)
        })
        viewModel.userNotFound.observe(this, Observer {
            showUserNotFound()
        })
    }

    private fun configureLoginButton() {
        btLogin.setOnClickListener {
            val userName = tiltEmail.text.toString()
            val userPassword = tiltPassword.text.toString()

            login(userName, userPassword)
        }
    }

    private fun showError(errorMessage: String) {
        Dialog.showError(this, errorMessage)
    }

    private fun showUserNotFound() {
        Toast.makeText(this, getString(R.string.email_and_password_incorrect), Toast.LENGTH_SHORT).show()
    }

    private fun login(email: String, password: String) {
        viewModel.getUserByEmailAndPassword(email, password)
    }

    private fun goToNextStep(user: User, classToOpen: Class<*>) {
        val intent = Intent(this, classToOpen)
        intent.putExtra(Constants.USER, Gson().toJson(user))
        startActivity(intent)
    }
}
