package com.example.userlogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore

class LoggedIn : AppCompatActivity(), View.OnClickListener {

    private lateinit var retrievedFromFirebase: Unit
    lateinit var listViewCourses: ListView
    lateinit var adapter: MyAdapter
    lateinit var btnSignOut : Button
    lateinit var btnPayment : Button
    lateinit var btnSave : Button
    lateinit var deleteAcc: Button
    lateinit var auth : FirebaseAuth
    lateinit var firebaseDatabase : FirebaseDatabase
    lateinit var addedCoursesCollection: DatabaseReference
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
        addedCoursesCollection = firebaseDatabase.getReference("Courses")
        val allCoursesCollection = db.collection("CoursesInfo")

        var allCourses: ArrayList<Course> = ArrayList()

        allCoursesCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    println("----------document----------")
                    println(document)
                    val course = document.toObject(Course::class.java)
                    course.courseName = document.id
                    println("----------course----------")
                    println(course)
                    allCourses.add(course)
                }

                listViewCourses = findViewById(R.id.list_courses)
                adapter = MyAdapter(this, allCourses)
                listViewCourses.adapter = adapter
                retrievedFromFirebase = retrieveFromFirebase(listViewCourses, userName)

            }
            .addOnFailureListener { exception ->
                println("get failed with:")
                println(exception)
            }

        println("---------------allCourses---------------")
        println(allCourses)

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
                for (course in adapter.arrayList) {
                    val objetoFirebase = Curso(course.courseName, course.isChecked)
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
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    println("DocumentSnapshot data: ${document.data}")
                    val coursesMap = document.data

                    if (coursesMap != null) {
                        for (entry in coursesMap) {
                            val courseName = entry.key
                            val isChecked = entry.value as? Boolean

                            if (isChecked != null) {
                                val course = (listViewCourses.adapter as MyAdapter).arrayList.find { it.courseName == courseName }
                                course?.isChecked = isChecked
                            }
                        }
                        adapter.notifyDataSetChanged()
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
        coursesMap: Map<Comparable<Nothing>, Boolean>
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
    val curso: Comparable<*>,
    val isChecked: Boolean
)