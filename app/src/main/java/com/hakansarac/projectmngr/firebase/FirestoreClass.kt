package com.hakansarac.projectmngr.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hakansarac.projectmngr.activities.MainActivity
import com.hakansarac.projectmngr.activities.SignInActivity
import com.hakansarac.projectmngr.activities.SignUpActivity
import com.hakansarac.projectmngr.models.User
import com.hakansarac.projectmngr.utils.Constants

/**
 * cloud firestore implementations are here
 */
class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * new user entry as a document to Users collection on cloud firestore
     */
    fun registerUser(activity : SignUpActivity,userInfo : User){
        mFireStore.collection(Constants.USERS)      //collection id name in cloud firestore database
            .document(getCurrentUserId())           //creating each document with uuid name
            .set(userInfo, SetOptions.merge())      //it is going to merge the user info whatever user info is passed to us.
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }//TODO: set addOnFailureListener
    }

    /**
     * when an user signed in,
     * get his data from cloud firestore.
     * In MainActivity, fill the navigation header with user image and user name from firebase.
     */
    fun signInUser(activity: Activity){
        mFireStore.collection(Constants.USERS)      //go to collection Users in cloud firestore
            .document(getCurrentUserId())           //go to current user's document
            .get()                                  //get the current user's fields
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser != null){
                    when(activity){
                        is SignInActivity -> activity.signInSuccess(loggedInUser)
                        is MainActivity -> activity.updateNavigationUserDetails(loggedInUser)
                    }
                }
            }.addOnFailureListener { exception ->
                when(activity){
                    is SignInActivity -> activity.hideProgressDialog()
                    is MainActivity -> activity.hideProgressDialog()
                }
                Log.e("SignInUser","Error writing document",exception)
            }
    }

    /**
     * returns uuid
     */
    fun getCurrentUserId():String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null)
            currentUserID = currentUser.uid
        return currentUserID
    }
}