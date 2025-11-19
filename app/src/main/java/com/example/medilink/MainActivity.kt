package com.example.medilink

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.medilink.R.layout.login

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla1)  // tu XML

        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        registerButton.setOnClickListener {
            setContentView(R.layout.choose_user)
        }

        loginButton.setOnClickListener {
            setContentView(login)
        }
    }
}
