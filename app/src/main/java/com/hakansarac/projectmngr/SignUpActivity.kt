package com.hakansarac.projectmngr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)  //it makes Sign Up Activity full screen

        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)     //create vector asset to show back icon on toolbar
        }
        toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }      //set the back icon to go to page back
    }
}