package com.lozada.christopher.integradormobile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class PostAdapter(private val activity: Activity, private var dataset: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val postLayout: View = view.findViewById(R.id.postLayout) // Referencia al ConstraintLayout raíz
        val postUsername: TextView = view.findViewById(R.id.postUsername)
        val contentPost: TextView = view.findViewById(R.id.contentPost)
        val fechaPost: TextView = view.findViewById(R.id.fechaPost)
        val shareBtn: Button = view.findViewById(R.id.shareBtn)
        val likeBtn: Button = view.findViewById(R.id.likeBtn)
        val likesCount: TextView = view.findViewById(R.id.likesCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = dataset[position]
        val likes = post.likes!!.toMutableList()
        var liked = likes.contains(auth.uid)

        holder.likesCount.text = "${likes.size} Me gusta"
        holder.postUsername.text = post.userName
        holder.contentPost.text = post.post

        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
        holder.fechaPost.text = sdf.format(post.date)
        setColor(liked, holder.likeBtn)

        holder.likeBtn.setOnClickListener {
            liked = !liked
            setColor(liked, holder.likeBtn)

            if (liked) likes.add(auth.uid!!)
            else likes.remove(auth.uid)

            val doc = db.collection("posts").document(post.uid!!)

            db.runTransaction {
                it.update(doc, "likes", likes)
                null
            }
        }

        holder.shareBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.post)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            activity.startActivity(shareIntent)
        }

        if (post.userName == auth.currentUser!!.displayName) {
            holder.postLayout.setOnLongClickListener {
                AlertDialog.Builder(activity).apply {
                    setTitle("Borrar la publicación")
                    setMessage("El post será eliminado permanentemente, ¿Estas seguro?")
                    setPositiveButton("Sí") { dialogInterface, i ->
                        db.collection("posts").document(post.uid!!).delete()
                            .addOnSuccessListener { }
                            .addOnFailureListener {
                                Utils.showError(activity, it.message.toString())
                            }
                    }
                    setNegativeButton("No", null)
                }.show()
                true
            }
        }
    }

    fun updateData(newData: List<Post>) {
        dataset = newData
        notifyDataSetChanged()
    }

    private fun setColor(liked: Boolean, likeBtn: Button) {
        if (liked) likeBtn.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        else likeBtn.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
    }
}
