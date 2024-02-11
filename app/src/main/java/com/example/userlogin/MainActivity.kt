package com.example.userlogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "DB_USER")

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnLogin : Button
    lateinit var btnSignUp : Button
    lateinit var username : EditText
    lateinit var password : EditText
    companion object {
        lateinit var dataStoreClass: DataStorePreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin = findViewById(R.id.login_btn)
        btnSignUp = findViewById(R.id.signup_btn)
        username = findViewById(R.id.username)
        password = findViewById(R.id.pass)

        btnLogin.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)

        dataStoreClass = DataStorePreferences(this)

        // loads data from preferences into in memoryDB
        lifecycleScope.launch(Dispatchers.IO) {
            dataStoreClass.getDB().collect() {dataBase ->
                db = dataStoreClass.convertJSONtoDB(dataBase)
        }
        }
    }

    override fun onClick(v: View?) {
        var usrname: String = username.text.toString()
        var pass: String = password.text.toString()
        when(v?.id) {
            R.id.login_btn -> {
                val usr: User? = db.users.find { it.username == usrname }
                if (usr != null) {
                    if (usr.password == pass.toString()) {
                        val intent = Intent(this, LoggedIn::class.java)
                        startActivity(intent)
                    }
                } else {
                    println('F')
                }
            }
            R.id.signup_btn -> {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }
        }
    }
}