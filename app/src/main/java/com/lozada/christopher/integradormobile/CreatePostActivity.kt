package com.lozada.christopher.integradormobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class CreatePostActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val newPostBtn: Button = findViewById(R.id.newPostBtn)
        val createNewPost: EditText = findViewById(R.id.createNewPost)

        newPostBtn.setOnClickListener {
            val postString = createNewPost.text.toString()
            val date = Date()
            val userName = auth.currentUser!!.displayName

            val post = Post(postString, date, userName)

            db.collection("posts").add(post)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener {
                    Utils.showError(this, it.message.toString())
                }
        }
    }
}
