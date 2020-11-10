package com.tugas.instagramclone.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugas.instagramclone.R
import com.tugas.instagramclone.adapter.PostAdapter
import com.tugas.instagramclone.model.Post


class HomeFragment : Fragment() {

    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var followingList : MutableList<Post>? = null
    var recyclerViewHome : RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewHome = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerViewHome = viewHome.findViewById(R.id.recycler_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerViewHome?.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context.let { it?.let { it -> PostAdapter(it, postList as ArrayList<Post>) } }
        recyclerViewHome?.adapter = postAdapter

        checkFollowing()

        return viewHome
    }

    private fun checkFollowing() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (followingList as ArrayList<*>).clear()

                    for (s in snapshot.children){
                        s.key.let { it?.let { it1 -> (followingList as ArrayList<String>).add(it1) } }
                    }
                    getPost()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getPost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for (s in snapshot.children){
                    val post = s.getValue(Post::class.java)
                    for (id in (followingList as ArrayList<String>)){

                        if (post!!.getPublisher() == id){
                            postList!!.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}