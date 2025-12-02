package com.example.medilink

import android.content.Intent
import android.os.Bundle
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
                    userName = name,
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

                            }
                            ProfileOptionType.LOCATION -> {

                            }
                            ProfileOptionType.SUBSCRIPTION -> {

                            }
                            ProfileOptionType.CLEAR_CACHE -> {

                            }
                            ProfileOptionType.CLEAR_HISTORY -> {

                            }
                            ProfileOptionType.LOGOUT -> {

                            }
                        }
                    }
                )
            }
        }
    }
}
