package com.hauscoding.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import javax.security.auth.callback.Callback


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private  lateinit var mGoogleSignInClient: GoogleSignInClient

    companion object
    {
        const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initActionBar()
        initFirebaseAuth()

        val btnSignIn: Button = findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener {
            val email: EditText = findViewById(R.id.etEmailSignIn)
            email.text.toString().trim()

            val password: EditText = findViewById(R.id.etPasswordSignIn)
            password.text.toString().trim()

            if (checkValidation(email, password))
            {
                loginToServer(email, password)
            }
        }

        val btnForgotPassword: TextView = findViewById(R.id.btnForgotPassword)
        btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        val btnGoogleSignIn: FrameLayout = findViewById(R.id.btnGoogleSignIn)
        btnGoogleSignIn.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val tbSignIn: Toolbar = findViewById(R.id.tbSignIn)
        tbSignIn.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN)
        {
            CustomDialog.showLoading(this)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try
            {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                firebaseAuth(credential)
            }
            catch (e: ApiException)
            {
                CustomDialog.hideLoading()
                Toast.makeText(this, "Sign-In cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginToServer(email: EditText, password: EditText) {
        val credential: AuthCredential = EmailAuthProvider.getCredential(email.text.toString(), password.text.toString())
        firebaseAuth(credential)
    }

    private fun firebaseAuth(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                CustomDialog.hideLoading()
                if (task.isSuccessful)
                {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                else
                {
                    Toast.makeText(this, "Sign-In failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                CustomDialog.hideLoading()
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkValidation(email: EditText, password: EditText): Boolean {
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
        else
        {
            return true
        }
        CustomDialog.hideLoading()
        return false
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

    private fun initActionBar() {
        val tbSignIn: Toolbar = findViewById(R.id.tbSignIn)
        setSupportActionBar(tbSignIn)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
}