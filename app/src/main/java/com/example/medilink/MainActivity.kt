package com.example.medilink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import com.example.medilink.ui.login.Login


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla1)  // tu XML

        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        registerButton.setOnClickListener {
            val user = Intent(this, ChooseUser::class.java)
            startActivity(user)
        }

        loginButton.setOnClickListener {
          //  val login = Intent(this, Login::class.java)
           // startActivity(login)
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }
    }
}
