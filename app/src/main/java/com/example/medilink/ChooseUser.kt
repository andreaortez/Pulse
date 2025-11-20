package com.example.medilink

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.medilink.ui.login.Registro

class ChooseUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_user)
    }

    fun regresarInicio(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun abrirRegistroFamiliar(view: View) {
        val register = Intent(this, Registro::class.java)
        register.putExtra("tipousuario", "FAMILIAR")
        startActivity(register)
    }

    fun abrirRegistroAdultoMayor(view: View) {
        val register = Intent(this, Registro::class.java)
        register.putExtra("tipousuario", "ADULTO_MAYOR")
        startActivity(register)
    }
}
