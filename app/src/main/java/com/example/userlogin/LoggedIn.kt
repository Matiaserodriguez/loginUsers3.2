package com.example.userlogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.get
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class LoggedIn : AppCompatActivity(), View.OnClickListener {

    private lateinit var retrievedFromFirebase: Unit
    lateinit var listViewCourses: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var btnSignOut : Button
    lateinit var btnPayment : Button
    lateinit var btnSave : Button
    lateinit var deleteAcc: Button
    lateinit var auth : FirebaseAuth
    lateinit var firebaseDatabase : FirebaseDatabase
    lateinit var collection: DatabaseReference
    lateinit var userName : String
    var arrayCourses : Course = Course()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        btnSignOut = findViewById(R.id.btn_sign_out)
        btnPayment = findViewById(R.id.btn_payment)
        btnSave = findViewById(R.id.btn_save)
        deleteAcc = findViewById(R.id.btn_delete_acc)

        auth = FirebaseAuth.getInstance()
        userName = if (auth.currentUser?.uid != null) auth.currentUser?.uid!! else intent.getStringExtra("USERNAME") ?: ""

        firebaseDatabase = FirebaseDatabase.getInstance()
        collection = firebaseDatabase.getReference("Courses")


        listViewCourses = findViewById(R.id.list_courses)
        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            arrayCourses.courseList
        )
        listViewCourses.adapter = adapter

        retrievedFromFirebase = retrieveFromFirebase(listViewCourses, userName)
        btnSignOut.setOnClickListener(this)
        btnPayment.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        deleteAcc.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_sign_out -> {
                this.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_save -> {
                val userUid = if (auth.currentUser?.uid != null) auth.currentUser?.uid else this.userName

                val cursosMap = mutableListOf<Curso>()
                for (i in 0 until adapter.count) {
                    val item = adapter.getItem(i)
                    val isChecked = listViewCourses.isItemChecked(i)
                    // listViewCourses.setItemChecked(i, true)

                    val objetoFirebase = Curso(item ?: "", isChecked)
                    cursosMap.add(objetoFirebase)
                }

                val mapaFirebase = cursosMap.associateBy({ it.curso }, { it.isChecked })
                println("------listaObjetosFirebase------")
                println(mapaFirebase)
                createOrUpdate(userUid, mapaFirebase)
            }
            R.id.btn_payment -> {
                val url = "https://www.mercadopago.com.ar"
                val intent = CustomTabsIntent.Builder()
                    .build()
                intent.launchUrl(this, Uri.parse(url))
            }
            R.id.btn_delete_acc -> {
                val googleSignInClient =
                    GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.DEFAULT_SIGN_IN)

                auth.currentUser?.delete()
                auth.signOut()
                googleSignInClient.revokeAccess()
                finish()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun retrieveFromFirebase(
        listViewCourses: ListView,
        userUid: String
    ) {
        val theCollection = db.collection("Courses").document(userUid)
        theCollection.get()
            .addOnSuccessListener {document ->
                if (document.exists()) {
                    println("DocumentSnapshot data: ${document.data}")
                    val coursesMap = document.data

                    for (i in 0 until adapter.count) {
                        val course = adapter.getItem(i)
                        val isChecked = document.getBoolean(course.toString())

                        if (isChecked != null) {
                            listViewCourses.setItemChecked(i, isChecked)
                        }
                    }

                    println(coursesMap)
                } else {
                    println("No such document")
                }
            }
            .addOnFailureListener { exception ->
                println("get failed with:")
                println(exception)
            }
    }

    private fun createOrUpdate(
        userUid: String?,
        coursesMap: Map<String, Boolean>
    ) {
        db.collection("Courses").document(userUid ?: "default").set(coursesMap)
            .addOnSuccessListener {
                showToast("Courses saved successfully")
            }
            .addOnFailureListener {
                showToast("Courses weren't saved")
            }
    }

    private fun signOut() {
        val googleSignInClient =
            GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.DEFAULT_SIGN_IN)

        auth.signOut()
        googleSignInClient.revokeAccess()

        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

data class Curso(
    val curso: String,
    val isChecked: Boolean
)