package com.hakansarac.projectmngr

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
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
    }
}