package com.example.medilink

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.ui.perfil.ListarUsuariosScreen

class ListarUsuariosActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = SessionManager.getUserId(this)
        val type = SessionManager.getUserType(this)

        if (id == null) {
            finish()
            return
        }

        if (type == null) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                ListarUsuariosScreen(
                    idUsuarioActual = "id_del_usuario_actual",
                    tipoUsuarioActual = "FAMILIAR", // o "ADULTO_MAYOR"
                    onBackClick = { finish() },
                    onAgregarAfiliado = {
                        // Navegar a pantalla de vinculación
                        val intent = Intent(this, VincularFamiliarActivity::class.java)
                        startActivity(intent)
                    },
                )
            }
        }
    }
}