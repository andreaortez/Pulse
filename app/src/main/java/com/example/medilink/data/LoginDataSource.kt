package com.example.medilink.data

import com.example.medilink.data.model.LoggedInUser
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.UUID

class LoginDataSource {

    // IMPORTANTE: desde el emulador, "localhost" de tu PC es 10.0.2.2
    private val baseUrl = "http://10.0.2.2:3000"

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val url = URL("$baseUrl/users/login")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }

            // JSON EXACTO que envías en Postman
            val jsonBody = JSONObject().apply {
                put("correo", username)
                put("contraseña", password)
            }.toString()

            // Enviar body
            connection.outputStream.use { os ->
                val bytes = jsonBody.toByteArray(StandardCharsets.UTF_8)
                os.write(bytes, 0, bytes.size)
            }

            val code = connection.responseCode

            if (code == HttpURLConnection.HTTP_OK) {
                // Leer respuesta OK
                val responseText =
                    connection.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(responseText)

                // { "message": "Sesión iniciada", "user": [ { ... } ] }
                val userJson = json.optJSONArray("user")?.optJSONObject(0)
                    ?: json.optJSONObject("user")

                val userId = userJson?.optString("_id", UUID.randomUUID().toString())
                    ?: UUID.randomUUID().toString()

                val nombre = userJson?.optString("nombre", username) ?: username

                val loggedInUser = LoggedInUser(
                    userId = userId,
                    displayName = nombre
                )

                return Result.Success(loggedInUser)
            } else {
                // Intentar leer mensaje de error del backend
                val errorMsg = try {
                    val errText =
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                    if (!errText.isNullOrEmpty()) {
                        JSONObject(errText).optString("message", "Error HTTP $code")
                    } else {
                        "Error HTTP $code"
                    }
                } catch (e: Exception) {
                    "Error HTTP $code"
                }

                return Result.Error(IOException(errorMsg))
            }

        } catch (e: Exception) {
            return Result.Error(IOException("Error al iniciar sesión", e))
        }
    }

    fun logout() {
    }
}
