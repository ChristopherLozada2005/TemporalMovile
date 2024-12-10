package com.lozada.christopher.integradormobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var rv: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rv = findViewById(R.id.rv)
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        listenForPosts()
    }

    private fun setupRecyclerView() {
        rv.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(this, mutableListOf())
        rv.adapter = postAdapter
    }

    private fun listenForPosts() {
        db.collection("posts")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Manejar error
                    return@addSnapshotListener
                }

                value?.let {
                    val posts = it.toObjects(Post::class.java)
                    posts.forEachIndexed { index, post ->
                        post.uid = it.documents[index].id
                    }
                    postAdapter.updateData(posts)
                }
            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_item -> {
                auth.signOut()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
