package com.example.userlogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity(), View.OnClickListener {

    lateinit var btnBack: Button
    lateinit var btnRegister: Button
    lateinit var username: EditText
    lateinit var password: EditText

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
        when (v?.id) {
            R.id.go_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            R.id.register -> {
                val usr: User = User(db, usr = usrname, pass = pass)
                println(usr)
                updateSettings(db = db)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun updateSettings(db: InMemoryDatabase) {
        lifecycleScope.launch(Dispatchers.IO) {
            MainActivity.dataStoreClass.insertDB(db)
        }
    }
}