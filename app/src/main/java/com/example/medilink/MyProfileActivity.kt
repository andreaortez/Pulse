package com.example.medilink

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.medilink.ui.perfil.ProfileScreen
import com.example.medilink.ui.perfil.ProfileOptionType

class MyProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = SessionManager.getUserId(this)
        val name = SessionManager.getUserName(this)
        val Lname = SessionManager.getUserLastName(this)
        val type = SessionManager.getUserType(this)

        if (id == null) {
            finish()
            return
        }

        if(type == null){
            return
        }

        setContent {
            MaterialTheme {
                ProfileScreen(
                    userName = "$name $Lname" ,
                    type = type,
                    onBackClick = { finish() },
                    onOptionClick = { option ->
                        when (option) {
                            ProfileOptionType.VINCULATE -> {
                                val intent = Intent(this, VincularFamiliarActivity::class.java)
                                startActivity(intent)
                            }
                            ProfileOptionType.EDITPROFILE -> {

                            }
                            ProfileOptionType.LIST -> {
                                val intent = Intent(this, ListarUsuariosActivity::class.java)
                                startActivity(intent)
                            }
                            ProfileOptionType.LOCATION -> {

                            }
                            ProfileOptionType.CLEAR_CACHE -> {

                            }
                            ProfileOptionType.LOGOUT -> {

                            }
                        }
                    }
                )
            }
        }

        fun AbrirActivity(view: View){
            val intent = Intent(this, View::class.java)
            startActivity(intent)
        }
    }
}
