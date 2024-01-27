package com.example.userlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.browser.customtabs.CustomTabsIntent

class LoggedIn : AppCompatActivity(), View.OnClickListener {

    lateinit var listViewCourses: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var btnSignOut : Button
    lateinit var btnPayment : Button
    var arrayCourses : Course = Course()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        btnSignOut = findViewById(R.id.btn_sign_out)
        btnPayment = findViewById(R.id.btn_payment)

        listViewCourses = findViewById(R.id.list_courses)
        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            arrayCourses.courseList
        )
        listViewCourses.adapter = adapter

        btnSignOut.setOnClickListener(this)
        btnPayment.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_sign_out -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_payment -> {
                val url = "https://www.mercadopago.com.ar"
                val intent = CustomTabsIntent.Builder()
                    .build()
                intent.launchUrl(this, Uri.parse(url))
            }
        }
    }
}