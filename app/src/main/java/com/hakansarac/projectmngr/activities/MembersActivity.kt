package com.hakansarac.projectmngr.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

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

        SendNotificationToUserAsyncTask(mBoardDetails.name,user.fcmToken)
    }

    /**
     * get the user details and add it to assignedTo list of boards in firebase
     * by calling assignMemberToBoard function
     */
    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }

    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String) : AsyncTask<Any,Void,String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result : String
            var connection: HttpURLConnection? = null
            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")
                connection.setRequestProperty(
                        Constants.FCM_AUTHORIZATION,
                        "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"      //TODO: FCM_SERVER_KEY must be initialized in Constants object
                )
                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE,"Assigned to the board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE,"You have been assigned to the board by ${mAssignedMembersList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_DATA,dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO,token)
                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult: Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line : String?
                    try{
                        while(reader.readLine().also {line = it} != null){
                            sb.append(line+"\n")
                        }
                    }catch(e: IOException){
                        e.printStackTrace()
                    }finally{
                        try{
                            inputStream.close()
                        }catch(e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e: Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }

    }
}