package com.hakansarac.projectmngr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hakansarac.projectmngr.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)  //it makes Sign Up Activity full screen

        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)     //create vector asset to show back icon on toolbar
        }
        toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }      //set the back icon to go to page back
    }

    /**
     * validate the sign up form and register user
     */
    private fun registerUser(){
        val name : String = editTextNameSignUp.text.toString().trim{ it <= ' '}    //to trim leading and trailing characters
        val email : String = editTextEmailSignUp.text.toString().trim{ it <= ' '}
        val password : String = editTextPasswordSignUp.text.toString().trim{ it <= ' '}
        /*
            Java's String#trim() removes all codepoints between '\u0000' (NULL) and '\u0020' (SPACE) from the start and end of the string.
            Kotlin's CharSequence.trim() removes only leading and trailing whitespace by default
            therefore we use .trim{ it <= ' '}
        */

        /**
         * sign up code
         */
        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))   //it is inherit from BaseActivity
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
                hideProgressDialog()
                if(task.isSuccessful){
                    val firebaseUser : FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    Toast.makeText(applicationContext,"$name, you have successfully registered the e-mail adress $registeredEmail",Toast.LENGTH_SHORT).show()

                    FirebaseAuth.getInstance().signOut()    //to disable the user to sign in directly.
                    finish()
                }else{
                    Toast.makeText(applicationContext,task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * checking sign up form,
     * all information must be given
     */
    private fun validateForm(name: String,email: String,password: String):Boolean{
        return when{
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name.")   //it is inherit from BaseActivity
                false
            }
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
     * if user clicks sign up button then do...
     */
    fun onClickButtonSignUp(view: View){
        registerUser()
    }
}