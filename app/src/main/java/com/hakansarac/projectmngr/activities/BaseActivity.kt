package com.hakansarac.projectmngr.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.hakansarac.projectmngr.R
import kotlinx.android.synthetic.main.dialog_progress.*

/**
 * base activity created to generate reuse functions.
 * those functions are used in other activities
 * all activities are created with inheritance of AppCompatActivity by default
 *
 * but if we want to use features of an activity in other activities;
 * we will inherit the activity to other activities, instead of AppCompatActivity
 */
class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false     //to close the app by pressing back twice
    private lateinit var mProgressDialog : Dialog       //to show progress bar while doing something in the background

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**
     * Setting the progress dialog
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.textViewProgressText.text = text
        mProgressDialog.show()
    }

    /**
     * to hide progress dialog
     */
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    /**
     * to get current user_id from FirebaseAuth system
     */
    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    /**
     * if somebody presses the back button twice, application should be closed
     */
    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBackToExitPressedOnce = false}, 2000)  //do that 2 seconds later
    }

    fun showErrorSnackBar(message:String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()
    }
}