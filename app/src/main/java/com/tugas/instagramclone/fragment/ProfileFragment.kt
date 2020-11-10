package com.tugas.instagramclone.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tugas.instagramclone.EditProfileActivity
import com.tugas.instagramclone.R
import com.tugas.instagramclone.adapter.MyImagesAdapter
import com.tugas.instagramclone.model.Post
import com.tugas.instagramclone.model.User
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var profilId: String
    private lateinit var firebaseUser: FirebaseUser

    var postListGrid: MutableList<Post>? = null
    var myImageAdapter: MyImagesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewProfile =  inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profilId = pref.getString("profileId", "none")!!
        }
        if (profilId == firebaseUser.uid){
                view?.btn_edit_profile?.text = "Edit Profile"
        }else if (profilId != firebaseUser.uid){
            cekFollowAndFollowingStatus()
        }

        var recyclerViewUploadImage: RecyclerView? = null
        recyclerViewUploadImage = viewProfile.findViewById(R.id.rv_profile)
        recyclerViewUploadImage.setHasFixedSize(true)

        val linearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImage?.layoutManager = linearLayoutManager

        postListGrid = ArrayList()
        myImageAdapter = context?.let { MyImagesAdapter(it, postListGrid as ArrayList<Post>) }
        recyclerViewUploadImage?.adapter = myImageAdapter

        viewProfile.btn_edit_profile.setOnClickListener {
            val getButtonText = view?.btn_edit_profile?.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, EditProfileActivity::class.java))

                getButtonText == "Follow" -> {
                    firebaseUser.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(profilId).setValue(true)
                    }
                    firebaseUser.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Followers").child(profilId).setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    firebaseUser.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(profilId).removeValue()
                    }
                    firebaseUser.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Followers").child(profilId).removeValue()
                    }
                }
            }
        }


        getFollowers()
        getFollowing()
        userInfo()
        myPost()
        return viewProfile
    }

    private fun myPost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    (postListGrid as ArrayList<Post>).clear()

                    for (s in snapshot.children){
                        val post = s.getValue(Post::class.java)

                        if (post?.getPublisher().equals(profilId)){
                            (postListGrid as ArrayList<Post>).add(post!!)
                        }
                        Collections.reverse(postListGrid)
                        myImageAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("users").child(profilId)

        usersRef.addValueEventListener(object : ValueEventListener  {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).into(view?.profile_pic)
                    view?.profile_username?.text =user?.getUsername()
                    view?.txt_profile_fullname?.text = user?.getFullname()
                    view?.bio?.text = user?.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profilId)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.txt_total_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profilId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.txt_total_followers?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun cekFollowAndFollowingStatus() {
        val followingRef = firebaseUser.uid.let {it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        view?.btn_edit_profile?.text = "Following"
                    }else {
                        view?.btn_edit_profile?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }


}