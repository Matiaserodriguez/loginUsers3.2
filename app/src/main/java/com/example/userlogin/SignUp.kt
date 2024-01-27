package com.example.userlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class SignUp : AppCompatActivity(), View.OnClickListener  {

    lateinit var btnBack : Button
    lateinit var btnRegister : Button
    lateinit var username : EditText
    lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnBack = findViewById(R.id.go_back)
        btnRegister = findViewById(R.id.register)
        username = findViewById(R.id.username)
        password = findViewById(R.id.pass)

        btnBack.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var usrname: String = username.text.toString()
        var pass: String = password.text.toString()
        when(v?.id) {
            R.id.go_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.register -> {
                val usr: User = User(db, usr=usrname, pass=pass)
                println(usr)
                val intent = Intent(this, LoggedIn::class.java)
                startActivity(intent)
                // val intent = Intent(this, SignUp::class.java)
                // startActivity(intent)
            }
        }
    }
}