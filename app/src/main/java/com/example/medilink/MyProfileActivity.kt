package com.example.medilink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.ui.perfil.ProfileScreen

class MyProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = SessionManager.getUserId(this)
        val name = SessionManager.getUserName(this)

        if (id == null) {
            finish()
            return
        }

        if(name == null){
            return
        }

        setContent {
            MaterialTheme {
                ProfileScreen(
                    idUsuario = id,
                    userName = name
                )
            }
        }
    }
}
