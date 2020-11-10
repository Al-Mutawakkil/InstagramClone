package com.tugas.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugas.instagramclone.R
import com.tugas.instagramclone.model.Comment
import com.tugas.instagramclone.model.User
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext:Context ,private val mComment: List <Comment>):
    RecyclerView.Adapter<CommentViewHolder>() {
    private var  firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_layout_comment ,parent,false)
        return CommentViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val myComment = mComment[position]
        holder.komentarnya.text = myComment.getComment()

        getInfoUserKomentar(holder.gambarprofile, holder.username_komentar, myComment.getPublisher())
    }

    private fun getInfoUserKomentar(gambarprofile: CircleImageView, usernameKomentar: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(publisher)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(gambarprofile)
                    usernameKomentar.text = user.getUsername()
                }
            }
        })
    }
}

class CommentViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
    var gambarprofile : CircleImageView =itemView.findViewById(R.id.gambar_list_komentar)
    var username_komentar : TextView =itemView.findViewById(R.id.username_profile_comment)
    var komentarnya : TextView =itemView.findViewById(R.id.commets_list)

}