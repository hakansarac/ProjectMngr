package com.hakansarac.projectmngr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.Board
import com.hakansarac.projectmngr.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,boardDocumentId)  //call the getBoardDetails with clicked board
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(title : String){
        setSupportActionBar(toolbarTaskListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }
        toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * get the board details and setup action bar
     */
    fun boardDetails(board : Board){
        hideProgressDialog()
        setupActionBar(board.name)
    }
}