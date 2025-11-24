package com.example.medilink.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medilink.ChooseUser
import com.example.medilink.MainActivity
import com.example.medilink.R
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class Registro : AppCompatActivity() {

    private var tipoUsuario: String = "ADULTO_MAYOR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)

        tipoUsuario = intent.getStringExtra("tipousuario") ?: "ADULTO_MAYOR"

        // Inputs
        val nameLayout = findViewById<TextInputLayout>(R.id.name)
        val lastNameLayout = findViewById<TextInputLayout>(R.id.lastName)
        val emailLayout = findViewById<TextInputLayout>(R.id.email)
        val passwordLayout = findViewById<TextInputLayout>(R.id.password)
        val confirmPasswordLayout = findViewById<TextInputLayout>(R.id.confirmPassword)

        val nameEdit = nameLayout.editText
        val lastNameEdit = lastNameLayout.editText
        val emailEdit = emailLayout.editText
        val passwordEdit = passwordLayout.editText
        val confirmPasswordEdit = confirmPasswordLayout.editText

        val btnRegistrar = findViewById<Button>(R.id.login)
        val btnIrALogin = findViewById<Button>(R.id.btnLogin)
        val loading = findViewById<ProgressBar>(R.id.loading)

        btnRegistrar.setOnClickListener {
            val nombre = nameEdit?.text?.toString()?.trim().orEmpty()
            val apellido = lastNameEdit?.text?.toString()?.trim().orEmpty()
            val correo = emailEdit?.text?.toString()?.trim().orEmpty()
            val contrasena = passwordEdit?.text?.toString()?.trim().orEmpty()
            val confirmar = confirmPasswordEdit?.text?.toString()?.trim().orEmpty()

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() ||
                contrasena.isEmpty() || confirmar.isEmpty()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loading.visibility = View.VISIBLE
            btnRegistrar.isEnabled = false

            registrarUsuarioEnBackend(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                contrasena = contrasena,
                tipoUsuario = tipoUsuario,
                onSuccess = {
                    runOnUiThread {
                        loading.visibility = View.GONE
                        btnRegistrar.isEnabled = true
                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                },
                onError = { mensaje ->
                    runOnUiThread {
                        loading.visibility = View.GONE
                        btnRegistrar.isEnabled = true
                        Toast.makeText(this, "Error: $mensaje", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        btnIrALogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun registrarUsuarioEnBackend(
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        tipoUsuario: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {
                val url = URL("http://10.0.2.2:3000/users/createUser")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                }

                val jsonBody = JSONObject().apply {
                    put("nombre", nombre)
                    put("apellido", apellido)
                    put("correo", correo)
                    put("contraseña", contrasena)
                    put("tipoUsuario", tipoUsuario)
                }.toString()

                connection.outputStream.use { os ->
                    val input = jsonBody.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val code = connection.responseCode

                if (code == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                    onSuccess()
                } else {
                    val errorMsg = try {
                        val errText = connection.errorStream?.bufferedReader()?.use { it.readText() }
                        if (!errText.isNullOrEmpty()) {
                            val json = JSONObject(errText)
                            json.optString("message", "Error HTTP $code")
                        } else {
                            "Error HTTP $code"
                        }
                    } catch (e: Exception) {
                        "Error HTTP $code"
                    }
                    onError(errorMsg)
                }

                connection.disconnect()
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }.start()
    }

    fun abrirUsuario(view: View) {
        val user = Intent(this, ChooseUser::class.java)
        startActivity(user)
    }

    fun abrirLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
