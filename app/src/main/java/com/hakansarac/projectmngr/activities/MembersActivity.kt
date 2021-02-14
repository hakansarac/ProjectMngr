package com.hakansarac.projectmngr.activities

import android.app.Activity
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
import com.hakansarac.projectmngr.adapters.MemberListItemsAdapter
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_add_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    private var anyChangesOnMemberList : Boolean = false

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

    override fun onBackPressed() {
        if(anyChangesOnMemberList){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
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
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
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
        mAssignedMembersList = list
        hideProgressDialog()

        recyclerViewMembersList.layoutManager = LinearLayoutManager(this)
        recyclerViewMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this, list)
        recyclerViewMembersList.adapter = adapter
    }

    /**
     * add the new member to mAssignedMembersList,
     * and setup members list.
     */
    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesOnMemberList = true
        setupMembersList(mAssignedMembersList)
    }

    /**
     * get the user details and add it to assignedTo list of boards in firebase
     * by calling assignMemberToBoard function
     */
    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }
}