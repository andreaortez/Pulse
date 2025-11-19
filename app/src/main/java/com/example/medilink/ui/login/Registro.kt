package com.example.medilink.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.medilink.ChooseUser
import com.example.medilink.MainActivity
import com.example.medilink.R
import com.example.medilink.R.layout.registro
import com.example.medilink.R.layout.pantalla1
import com.example.medilink.databinding.RegistroBinding

class Registro : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: RegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ INFLAR EL XML registro.xml
        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(R.layout.registro)

        // 2️⃣ VIEWMODEL (si lo sigues usando)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // 3️⃣ OBSERVERS DEL VIEWMODEL (si quieres dejarlos)
        loginViewModel.loginFormState.observe(this@Registro, Observer { loginState ->
            val state = loginState ?: return@Observer

            binding.login.isEnabled = state.isDataValid

            if (state.usernameError != null) {
                binding.email?.error = getString(state.usernameError)
            }
        })

        loginViewModel.loginResult.observe(this@Registro, Observer { loginResult ->
            val result = loginResult ?: return@Observer

            binding.loading.visibility = View.GONE
            if (result.error != null) {
                showLoginFailed(result.error)
            }
            if (result.success != null) {
                updateUiWithUser(result.success)
            }
            setResult(Activity.RESULT_OK)
            finish()
        })

    }

    fun abrirUsuario(view: View) {
        val user = Intent(this, ChooseUser::class.java)
        startActivity(user)
    }

    fun abrirLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
