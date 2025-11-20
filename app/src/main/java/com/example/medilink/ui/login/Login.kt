package com.example.medilink.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.medilink.ChooseUser
import com.example.medilink.MainActivity
import com.example.medilink.R
import com.google.android.material.textfield.TextInputLayout

class Login : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val emailLayout = findViewById<TextInputLayout>(R.id.email)
        val passwordLayout = findViewById<TextInputLayout>(R.id.password)

        val emailEdit: EditText? = emailLayout.editText
        val passwordEdit: EditText? = passwordLayout.editText

        val loginButton = findViewById<Button>(R.id.login)
        val loading = findViewById<View>(R.id.loading)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginResult.observe(this) { loginResult ->
            val result = loginResult ?: return@observe

            loading.visibility = View.GONE

            if (result.error != null) {
                showLoginFailed(result.error)
            }
            if (result.success != null) {
                updateUiWithUser(result.success)
                setResult(Activity.RESULT_OK)
                // cerramos la pantalla al tener éxito
                finish()
            }
        }

        passwordEdit?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val correo = emailEdit?.text?.toString().orEmpty()
                val contra = passwordEdit.text?.toString().orEmpty()

                if (correo.isNotBlank() && contra.isNotBlank()) {
                    loading.visibility = View.VISIBLE
                    loginViewModel.login(correo, contra)
                } else {
                    Toast.makeText(
                        this,
                        "Completa correo y contraseña",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            false
        }

        loginButton.setOnClickListener {
            val correo = emailEdit?.text?.toString().orEmpty()
            val contra = passwordEdit?.text?.toString().orEmpty()

            if (correo.isBlank() || contra.isBlank()) {
                Toast.makeText(
                    this,
                    "Completa correo y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            loading.visibility = View.VISIBLE
            loginViewModel.login(correo, contra)
        }
    }

    fun regresarInicio(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun abrirRegistro(view: View) {
        startActivity(Intent(this, ChooseUser::class.java))
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        Toast.makeText(
            applicationContext,
            "$welcome ${model.displayName}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : android.text.TextWatcher {
        override fun afterTextChanged(editable: android.text.Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
