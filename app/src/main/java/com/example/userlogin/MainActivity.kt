package com.example.userlogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "DB_USER")

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnLogin : Button
    lateinit var btnSignUp : Button
    lateinit var btnGoogle : Button
    lateinit var username : EditText
    lateinit var password : EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    companion object {
        lateinit var dataStoreClass: DataStorePreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle = findViewById(R.id.google_btn)
        btnLogin = findViewById(R.id.login_btn)
        btnSignUp = findViewById(R.id.signup_btn)
        username = findViewById(R.id.username)
        password = findViewById(R.id.pass)

        btnLogin.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)
        btnGoogle.setOnClickListener(this)

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
                        intent.putExtra("USERNAME", usr.username)
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
            R.id.google_btn -> {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
        }
    }

    private fun handleResults(task : Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account : GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, LoggedIn::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}