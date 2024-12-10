package com.lozada.christopher.integradormobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.lozada.christopher.integradormobile.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.registerBtn.setOnClickListener {
            val nombre = binding.nameRegister.text.toString()
            val apellidos = binding.apellidosRegister.text.toString()
            val edad = binding.edadRegister.text.toString()
            val edadnumber = edad.toIntOrNull() ?: 0
            val genero = binding.genderSpinner.selectedItem.toString()
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$nombre $apellidos")
                        .build()

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val newUser = User(
                                    nombre = nombre,
                                    apellidos = apellidos,
                                    edad = edadnumber,
                                    genero = genero,
                                    email = email
                                )

                                val db = FirebaseFirestore.getInstance()
                                db.collection("users").document(user.uid)
                                    .set(newUser)
                                    .addOnSuccessListener {
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Cuenta Creada")
                                            setMessage("Tu cuenta ha sido creada correctamente")
                                            setPositiveButton("Aceptar") { dialog, _ ->
                                                dialog.dismiss()
                                                finish()
                                            }
                                        }.show()
                                    }

                                    .addOnFailureListener {
                                        Utils.showError(this, it.message.toString())
                                    }
                            }
                        }
                }
                .addOnFailureListener {
                    Utils.showError(this, it.message.toString())
                }
        }

        binding.goToLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
