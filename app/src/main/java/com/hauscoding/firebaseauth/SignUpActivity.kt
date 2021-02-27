package com.hauscoding.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initActionBar()

        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val email: EditText = findViewById(R.id.etEmailSignUp)
            email.text.toString().trim()

            val password: EditText = findViewById(R.id.etPasswordSignUp)
            password.text.toString().trim()

            val confirmPassword: EditText = findViewById(R.id.etConfirmPasswordSignUp)
            confirmPassword.text.toString().trim()

            CustomDialog.showLoading(this)
            if (checkValidation(email, password, confirmPassword))
            {
                registerToServer(email, password)
            }

        }

        val tbSignUp: Toolbar = findViewById(R.id.tbSignUp)
        tbSignUp.setNavigationOnClickListener {
            finish()
        }
    }

    private fun registerToServer(email: EditText, password: EditText) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {task ->
                CustomDialog.hideLoading()
                if (task.isSuccessful)
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { exception ->
                CustomDialog.hideLoading()
                Toast.makeText(this, "Registration is failed, " + exception.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun checkValidation(
        email: EditText,
        password: EditText,
        confirmPassword: EditText
    ): Boolean {
        if (email.text.toString().isEmpty())
        {
            email.error = "Please field your email"
            email.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches())
        {
            email.error = "Please use valid email"
            email.requestFocus()
        }
        else if (password.text.toString().isEmpty())
        {
            password.error = "Please field your password"
            password.requestFocus()
        }
        else if (confirmPassword.text.toString().isEmpty())
        {
            confirmPassword.error = "Please field your confirm password"
            confirmPassword.requestFocus()
        }
        else if (password.text.toString() != confirmPassword.text.toString())
        {
            password.error = "your password not matches with confirm password"
            confirmPassword.error = "your password not matches with confirm password"

            password.requestFocus()
            confirmPassword.requestFocus()
        }
        else
        {
            password.error = null
            confirmPassword.error = null
            return true
        }

        CustomDialog.hideLoading()
        return false
    }

    private fun initActionBar() {
        val tbSignUp: Toolbar = findViewById(R.id.tbSignUp)
        setSupportActionBar(tbSignUp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
}