package com.example.esp_led

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.login_screen.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
    }

    fun logIn(view: View) {

        val username = findViewById<EditText>(R.id.loginText)
        val userText = username.text.toString()
        val password = findViewById<EditText>(R.id.passwordText)
        val passText = password.text.toString()
        if(userText == "admin" && passText == "123") {
            val loginIntent = Intent(this, DeviceList::class.java)
            loginIntent.putExtra(DeviceList.USER, userText)

            startActivity(loginIntent)
        } else {
            Toast.makeText(this, "Password or Login incorrect!", Toast.LENGTH_LONG).show()
            password.setText("")
        }
    }
}