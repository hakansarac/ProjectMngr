package com.hakansarac.projectmngr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.User
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()
        FirestoreClass().loadUserData(this)
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * set the user details in My Profile page.
     */
    fun setUserDataInUI(user : User){
        //https://github.com/bumptech/glide
        Glide.with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(imageViewUserImageProfile)

        editTextNameProfile.setText(user.name)
        editTextEmailProfile.setText(user.email)
        if(user.mobile != 0L)
            editTextMobileProfile.setText(user.mobile.toString())
    }
}