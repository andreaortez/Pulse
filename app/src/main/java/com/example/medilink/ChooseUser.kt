package com.example.medilink

import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import android.view.View
import android.app.Activity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.medilink.R.layout.choose_user
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.medilink.MainActivity
import com.example.medilink.R
import com.example.medilink.ui.login.Registro

class ChooseUser : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.choose_user)  // tu XML
    }

    fun regresarInicio(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun abrirRegistro(view: View) {
        val register = Intent(this, Registro::class.java)
        startActivity(register)
    }
}



