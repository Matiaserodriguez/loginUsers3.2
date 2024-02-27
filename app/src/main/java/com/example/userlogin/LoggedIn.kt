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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoggedIn : AppCompatActivity(), View.OnClickListener {

    lateinit var listViewCourses: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var btnSignOut : Button
    lateinit var btnPayment : Button
    lateinit var btnSave : Button
    lateinit var auth : FirebaseAuth
    lateinit var firebaseDatabase : FirebaseDatabase
    lateinit var collection: DatabaseReference
    var arrayCourses : Course = Course()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        btnSignOut = findViewById(R.id.btn_sign_out)
        btnPayment = findViewById(R.id.btn_payment)
        btnSave = findViewById(R.id.btn_save)

        auth = FirebaseAuth.getInstance()

        firebaseDatabase = FirebaseDatabase.getInstance()
        collection = firebaseDatabase.getReference("courses")

        listViewCourses = findViewById(R.id.list_courses)
        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            arrayCourses.courseList
        )
        listViewCourses.adapter = adapter

        btnSignOut.setOnClickListener(this)
        btnPayment.setOnClickListener(this)
        btnSave.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_sign_out -> {
                this.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_save -> {
                val userUid = auth.currentUser?.uid
                createOrUpdate(userUid, collection, listViewCourses)
            }
            R.id.btn_payment -> {
                val url = "https://www.mercadopago.com.ar"
                val intent = CustomTabsIntent.Builder()
                    .build()
                intent.launchUrl(this, Uri.parse(url))
            }
        }
    }

    private fun createOrUpdate(
        userUid: String?,
        collection: DatabaseReference,
        listViewCourses: ListView
    ) {
        val coursesId = collection.push().key
        collection.child(coursesId ?: "").setValue(listViewCourses)
    }

    private fun signOut() {
        val googleSignInClient =
            GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.DEFAULT_SIGN_IN)

        auth.signOut()
        googleSignInClient.revokeAccess()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}