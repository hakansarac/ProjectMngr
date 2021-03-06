package com.hakansarac.projectmngr.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)  //it makes Splash Activity full screen

        /**
            assets folder created and carbon font that downloaded from https://www.1001fonts.com/carbon-font.html added to assets folder.
            this font is free for commercial use. be careful about licence conditions.
        */
        val typeface : Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf")
        textViewAppName.typeface = typeface


        /**
            After 2500 milliseconds, open IntroActivity.
         */
        Handler().postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){                                               //it means FirebaseAuth.getInstance().currentUser is not null
                startActivity(Intent(this, MainActivity::class.java))       //if user has signed in already then go MainActivity directly
            }else{
                startActivity(Intent(this, IntroActivity::class.java))      //if user has not signed in yet then go IntroActivity
            }
            finish()
        },2000)
    }
}