package com.hakansarac.projectmngr.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)  //it makes Sign In Activity full screen
        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarSignInActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)     //create vector asset to show back icon on toolbar
        }
        toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }      //set the back icon to go to page back
    }

    /**
     * validate the sign in form and sign in user
     */
    private fun signInRegisteredUser(){
        val email = editTextEmailSignIn.text.toString().trim{ it <= ' '}
        val password = editTextPasswordSignIn.text.toString().trim{ it <= ' '}
        /*
            Java's String#trim() removes all codepoints between '\u0000' (NULL) and '\u0020' (SPACE) from the start and end of the string.
            Kotlin's CharSequence.trim() removes only leading and trailing whitespace by default
            therefore we use .trim{ it <= ' '}
        */

        /**
         * sign in code
         */
        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) { task ->
                hideProgressDialog()
                if(task.isSuccessful){
                    FirestoreClass().signInUser(this)
                }else{
                    Log.w("Sign In","signInWithEmail:failure",task.exception)
                    Toast.makeText(applicationContext,"Authentication failed.",Toast.LENGTH_SHORT).show()
                }
            } //TODO: set addOnFailureListener
        }
    }


    /**
     * checking sign in form,
     * all information must be given
     */
    private fun validateForm(email: String,password: String):Boolean{
        return when{
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an e-mail address.")    //it is inherit from BaseActivity
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password.")   //it is inherit from BaseActivity
                false
            }
            else -> true
        }
    }

    /**
     * if user signed in successfully
     * the user parameter is from the User class we created
     */
    fun signInSuccess(user : User){
        hideProgressDialog()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * if user clicks sign in button then do...
     */
    fun onClickButtonSignIn(view : View){
        signInRegisteredUser()
    }
}