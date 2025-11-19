package com.example.medilink.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.medilink.R
import com.example.medilink.databinding.RegistroBinding

class Registro : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: RegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ INFLAR EL XML registro.xml
        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            if (state.passwordError != null) {
                binding.password.error = getString(state.passwordError)
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

        // 4️⃣ BOTÓN BACK
        binding.btnBackRegistro!!.setOnClickListener {
            setContentView(R.layout.pantalla1)
        }

        // 5️⃣ TEXTO "Inicia Sesión"
        binding.tvIniciarSesion!!.setOnClickListener {
            setContentView(R.layout.login)
        }

        // 6️⃣ BOTÓN "Registrarme"
        binding.login.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            performRegistration()
        }

        // 7️⃣ LISTENERS DE TEXTO
        setupTextChangeListeners()

        // 8️⃣ Acción DONE en confirmar contraseña
        binding.confirmPassword?.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    performRegistration()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupTextChangeListeners() {
        binding.name?.afterTextChanged {
            validateForm()
        }

        binding.lastName?.afterTextChanged {
            validateForm()
        }

        binding.email?.afterTextChanged {
            validateForm()
        }

        binding.password.afterTextChanged {
            validateForm()
            validatePasswordMatch()
        }

        binding.confirmPassword?.afterTextChanged {
            validatePasswordMatch()
            validateForm()
        }
    }

    private fun validateForm() {
        val name = binding.name?.text.toString()
        val lastName = binding.lastName?.text.toString()
        val email = binding.email?.text.toString()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword?.text.toString()

        val isNameValid = name.isNotBlank()
        val isLastNameValid = lastName.isNotBlank()
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6
        val doPasswordsMatch = password == confirmPassword && confirmPassword.isNotBlank()

        binding.login.isEnabled =
            isNameValid && isLastNameValid && isEmailValid && isPasswordValid && doPasswordsMatch

        if (!isNameValid && name.isNotBlank()) binding.name?.error = "Nombre requerido" else binding.name?.error = null
        if (!isLastNameValid && lastName.isNotBlank()) binding.lastName?.error = "Apellido requerido" else binding.lastName?.error = null
        if (!isEmailValid && email.isNotBlank()) binding.email?.error = "Email inválido" else binding.email?.error = null
        if (!isPasswordValid && password.isNotBlank()) binding.password.error = "Mínimo 6 caracteres" else binding.password.error = null
    }

    private fun validatePasswordMatch() {
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword?.text.toString()

        if (confirmPassword.isNotBlank() && password != confirmPassword) {
            binding.confirmPassword?.error = "Las contraseñas no coinciden"
        } else {
            binding.confirmPassword?.error = null
        }
    }

    private fun performRegistration() {
        val email = binding.email?.text.toString()
        // Aquí iría tu lógica real de registro
        Toast.makeText(this, "Registro exitoso para: $email", Toast.LENGTH_SHORT).show()
        binding.loading.visibility = View.GONE
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
