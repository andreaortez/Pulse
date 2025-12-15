package com.example.medilink.data

import com.example.medilink.BuildConfig
import com.example.medilink.data.model.LoggedInUser
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.UUID

class LoginDataSource {



    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val url = URL(BuildConfig.USERS_URL+"/login")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 7000
                readTimeout = 7000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }

            val jsonBody = JSONObject().apply {
                put("correo", username)
                put("contraseña", password)
            }.toString()

            connection.outputStream.use { os ->
                val bytes = jsonBody.toByteArray(StandardCharsets.UTF_8)
                os.write(bytes, 0, bytes.size)
            }

            val code = connection.responseCode

            if (code == HttpURLConnection.HTTP_OK) {

                val responseText =
                    connection.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(responseText)

                val userJson = json.optJSONArray("user")?.optJSONObject(0)
                    ?: json.optJSONObject("user")

                val userId = userJson?.optString("_id") ?: UUID.randomUUID().toString()
                val nombre = userJson?.optString("nombre", username) ?: username
                val apellido = userJson?.optString("apellido", "") ?: ""
                val tipoUsuario = userJson?.optString("tipoUsuario", "") ?: ""

                val phone = userJson?.optString("num_telefono", null)
                val age: Int? = if (userJson != null && userJson.has("edad")) {
                    userJson.optInt("edad")
                } else null

                val loggedInUser = LoggedInUser(
                    userId = userId,
                    displayName = nombre,
                    lastName = apellido,
                    userType = tipoUsuario,
                    email = username,
                    phone = phone,
                    age = age
                )

                return Result.Success(loggedInUser)
            } else {
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
            e.printStackTrace()
            return Result.Error(IOException("Error al iniciar sesión", e))
        }
    }

    fun logout() {}
}
