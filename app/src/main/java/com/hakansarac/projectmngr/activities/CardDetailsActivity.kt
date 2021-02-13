package com.hakansarac.projectmngr.activities

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.dialogs.LabelColorListDialog
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.Board
import com.hakansarac.projectmngr.models.Card
import com.hakansarac.projectmngr.models.Task
import com.hakansarac.projectmngr.models.User
import com.hakansarac.projectmngr.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_members.toolbarMembersActivity

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()
        setupActionBar()
        editTextNameCardDetails.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        editTextNameCardDetails.setSelection(editTextNameCardDetails.text.toString().length)    //to set the focus directly onto the ending of the length of the text.

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }
        toolbarCardDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionDeleteCard -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * getting data from TaskListActivity,
     * to use selected card details.
     */
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    /**
     * if a card has changed and the task list has updated successfully
     */
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * update the card details
     */
    private fun updateCardDetails(){
        val card = Card(
                editTextNameCardDetails.text.toString(),
                mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
                mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
                mSelectedColor
        )
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card   //card name can be changed

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    /**
     * delete the card from the task.
     */
    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    /**
     * if the user clicks on Update button,
     * update card details
     */
    fun onClickButtonUpdateCardDetails(view: View){
        if(editTextNameCardDetails.text.toString().isNotEmpty()){
            updateCardDetails()
        }else{
            Toast.makeText(this,"Please enter a card name.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card,cardName))      //to use cardName add \'%1$s\' to string
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)){ dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /**
     * preparing of colors list
     */
    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }

    /**
     * set the background with selected color
     */
    private fun setColor(){
        textViewSelectLabelColor.text = ""
        textViewSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    /**
     * show the colors list dialog to user select the card color
     */
    private fun labelColorsListDialog(){
        val colorsList : ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(this,colorsList,resources.getString(R.string.str_select_label_color),mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    /**
     * if the user clicks on the selected color,
     * show the colors list dialog
     */
    fun onClickTextViewSelectLabelColor(view: View){
        labelColorsListDialog()
    }
}