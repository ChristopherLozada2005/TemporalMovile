package com.lozada.christopher.integradormobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.lozada.christopher.integradormobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (auth.currentUser != null ) startApp()



        binding.loginBtn.setOnClickListener {
            val email = binding.emailLoginbtn.text.toString()
            val password = binding.passwordLoginbtn.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    startApp()
                }
                .addOnFailureListener{
                    Utils.showError(this, it.message.toString())
                }
        }

        binding.crearCuentaTv.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startApp(){
        val intent = Intent(this, MainActivity::class.java )
        startActivity(intent)
        finish()
    }
}
