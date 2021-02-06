package com.hakansarac.projectmngr.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hakansarac.projectmngr.activities.*
import com.hakansarac.projectmngr.models.Board
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
     * get the signed in user data from firebase.
     */
    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)      //go to collection Users in cloud firestore
            .document(getCurrentUserId())           //go to current user's document
            .get()                                  //get the current user's fields
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser != null){
                    when(activity){
                        is SignInActivity -> activity.signInSuccess(loggedInUser)
                        is MainActivity -> activity.updateNavigationUserDetails(loggedInUser)
                        is MyProfileActivity -> activity.setUserDataInUI(loggedInUser)
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

    /**
     * update the user details on Firebase
     */
    fun updateUserProfileData(activity: MyProfileActivity,userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)          //go to collection Users in cloud firestore
                .document(getCurrentUserId())           //go to current user's document
                .update(userHashMap)                    //update the user document with My Profile inputs
                .addOnSuccessListener {
                    Log.i(activity.javaClass.simpleName,"Profile data updated successfully")
                    Toast.makeText(activity,"Profile updated successfully.",Toast.LENGTH_SHORT).show()
                    activity.profileUpdateSuccess()
                }.addOnFailureListener { exception ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while creating board.",exception)
                    Toast.makeText(activity,"Error while updating the profile.",Toast.LENGTH_SHORT).show()
                }
    }

    fun createBoard(activity:CreateBoardActivity,board : Board){
        mFireStore.collection(Constants.BOARDS)
                .document() //random id
                .set(board, SetOptions.merge())
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,"Board created successfully.")
                    Toast.makeText(activity,"Board created successfully.",Toast.LENGTH_SHORT).show()
                    activity.boardCreatedSuccessfully()
                }.addOnFailureListener { exception ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while creating a board.",exception)
                }
    }
}