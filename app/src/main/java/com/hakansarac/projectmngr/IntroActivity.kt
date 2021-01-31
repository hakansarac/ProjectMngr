package com.hakansarac.projectmngr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)  //it makes Intro Activity full screen
    }

    //if user clicks SignUp button then...
    fun onClickButtonSignUpIntro(view : View){
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
    }

    //if user clicks SignIn button then...
    fun onClickButtonSignInIntro(view : View){
        val intent = Intent(this,SignInActivity::class.java)
        startActivity(intent)
    }

}