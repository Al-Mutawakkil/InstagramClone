package com.tugas.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugas.instagramclone.DetailPostActivity
import com.tugas.instagramclone.R
import com.tugas.instagramclone.model.Post

class MyImagesAdapter(private val myContext: Context, private val mPost : List<Post>)
    : RecyclerView.Adapter<MyImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_post_layout, parent,false)
        return MyImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val mPost = mPost[position]

        Glide.with(myContext).load(mPost.getPostImage()).into(holder.postImageGrid)

        holder.itemView.setOnClickListener{
            myContext.startActivity(Intent(myContext, DetailPostActivity::class.java)
                .putExtra("username", mPost.getPublisher())
                .putExtra("postId", mPost.getPostId())
                .putExtra("postimage", mPost.getPostImage())
                .putExtra("description", mPost.getDescription())
            )
        }

    }
}

class MyImagesViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    var postImageGrid : ImageView = itemView.findViewById(R.id.post_image_grid)
}
