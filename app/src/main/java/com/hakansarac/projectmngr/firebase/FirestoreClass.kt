package com.hakansarac.projectmngr.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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


    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)      //go to collection Users in cloud firestore
            .document(getCurrentUserId())           //go to current user's document
            .get()                                  //get the current user's fields
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser != null)
                    activity.signInSuccess(loggedInUser)
            }//TODO: set addOnFailureListener
    }

    /**
     * returns uuid
     */
    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}