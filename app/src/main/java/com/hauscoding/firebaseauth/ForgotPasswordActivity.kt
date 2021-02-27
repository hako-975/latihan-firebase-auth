package com.hauscoding.firebaseauth

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initActionBar()

        val btnSendEmail: Button = findViewById(R.id.btnSendEmail)

        btnSendEmail.setOnClickListener {
            val etEMailForgotPassword: EditText = findViewById(R.id.etEmailForgotPassword)
            etEMailForgotPassword.text.toString().trim()
            if (etEMailForgotPassword.text.toString().trim().isEmpty())
            {
                etEMailForgotPassword.error = "Please field your email"
                etEMailForgotPassword.requestFocus()
                return@setOnClickListener
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(etEMailForgotPassword.text.toString().trim()).matches())
            {
                etEMailForgotPassword.error = "Please use valid email"
                etEMailForgotPassword.requestFocus()
                return@setOnClickListener
            }
            else
            {
                forgotPassword(etEMailForgotPassword.text.toString())
            }
        }

        val tbForgotPassword: Toolbar = findViewById(R.id.tbForgotPassword)

        tbForgotPassword.setNavigationOnClickListener {
            finish()
        }
    }

    private fun forgotPassword(etEMailForgotPassword: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(etEMailForgotPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "Your reset has been send to your email", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finishAffinity()
                }
                else
                {
                    Toast.makeText(this, "Failed reset password", Toast.LENGTH_SHORT).show()
                }
            }

            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun initActionBar() {
        val tbForgotPassword: Toolbar = findViewById(R.id.tbForgotPassword)
        setSupportActionBar(tbForgotPassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
}