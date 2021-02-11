package com.hakansarac.projectmngr.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.Board
import com.hakansarac.projectmngr.models.User
import com.hakansarac.projectmngr.utils.Constants
import com.projemanag.adapters.MemberListItemsAdapter
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_add_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
        }
        setupActionBar()
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarMembersActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionAddMember -> {
                dialogAddMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * preparing Add Member Dialog functionality
     */
    private fun dialogAddMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_member)
        dialog.textViewAddMember.setOnClickListener {
            val email = dialog.editTextEmailAddMember.text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                //TODO: implement adding member
            }else{
                Toast.makeText(this@MembersActivity,"Please enter an email.",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.textViewCancelAddMember.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    /**
     * prepare the members list recycler view
     */
    fun setupMembersList(list: ArrayList<User>){
        hideProgressDialog()

        recyclerViewMembersList.layoutManager = LinearLayoutManager(this)
        recyclerViewMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this, list)
        recyclerViewMembersList.adapter = adapter
    }
}