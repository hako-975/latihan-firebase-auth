package com.hauscoding.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebaseAuth()
        getData()


        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            auth.signOut()
            mGoogleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
            }

            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun getData() {
        val user = auth.currentUser
        if (user != null)
        {
            val tvEmail: TextView = findViewById(R.id.tvEmail)
            tvEmail.text = user.email
            if (user.email == null)
            {
                tvEmail.text = user.displayName
            }
        } else {
            val tvEmail: TextView = findViewById(R.id.tvEmail)
            tvEmail.text = "email"
        }
    }

    private fun initFirebaseAuth() {
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

}