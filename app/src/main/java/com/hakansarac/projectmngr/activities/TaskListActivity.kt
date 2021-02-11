package com.hakansarac.projectmngr.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.adapters.TaskListItemsAdapter
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.Board
import com.hakansarac.projectmngr.models.Card
import com.hakansarac.projectmngr.models.Task
import com.hakansarac.projectmngr.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentId : String

    companion object{
        const val MEMBER_REQUEST_CODE : Int = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)  //call the getBoardDetails with clicked board
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionMembers -> {
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBER_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * if a new member added,
     * reload the task activity.
     * otherwise do not do anything for optimization of database requests.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBER_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this,mBoardDocumentId)  //call the getBoardDetails with clicked board
        }else{
            Log.i("Cancelled","Cancelled")
        }
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarTaskListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * get the board details
     * and set action bar and recyclerViewTaskList
     */
    fun boardDetails(board : Board){
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList) //it will be Add List textView(selectable item like a button)
        recyclerViewTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        recyclerViewTaskList.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this,board.taskList)
        recyclerViewTaskList.adapter = adapter
    }

    /**
     * when task list updated successfully,
     * get the board details to show in TaskListActivity
     */
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()    //hide when loaded activity successfully
        showProgressDialog(resources.getString(R.string.please_wait))    //show while trying to get something from FireStore
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    /**
     * creating new task and adding to the task list
     */
    fun createTaskList(taskListName : String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    /**
     * if the user edits a task exist one,
     * then update the task and the task list.
     */
    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    /**
     * if the user deletes a task,
     * then update the task list
     */
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    /**
     * add new card to the task
     * and update task
     */
    fun addCardToTaskList(position: Int,cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUsersList : ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())
        val card = Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUsersList)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(mBoardDetails.taskList[position].title, mBoardDetails.taskList[position].createdBy, cardsList)
        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun cardDetails(taskListPosition : Int, cardPosition: Int){
        val intent = Intent(this, CardDetailsActivity::class.java)
        startActivity(intent)
    }
}