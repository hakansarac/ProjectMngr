package com.hakansarac.projectmngr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hakansarac.projectmngr.R
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*

class CreateBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        setupActionBar()
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }
}