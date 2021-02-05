package com.hakansarac.projectmngr.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.User
import com.hakansarac.projectmngr.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri : Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()
        FirestoreClass().loadUserData(this)
    }

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * set the user details in My Profile page.
     */
    fun setUserDataInUI(user : User){
        mUserDetails = user

        //https://github.com/bumptech/glide
        Glide.with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(imageViewUserImageProfile)

        editTextNameProfile.setText(user.name)
        editTextEmailProfile.setText(user.email)
        if(user.mobile != 0L)
            editTextMobileProfile.setText(user.mobile.toString())
    }

    /**
     * check the changes and update userHashMap
     * and call the FirestoreClass().updateUserProfileData() function to update the user details on Firebase
     */
    fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()
        var anyChanges = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {     //if the image changed
            userHashMap[Constants.IMAGE] = mProfileImageURL                             //then add the image to userHashMap to update
            anyChanges = true
        }
        if(editTextNameProfile.text.toString() != mUserDetails.name) {                  //if the name changed
            userHashMap[Constants.NAME] = editTextNameProfile.text.toString()           //then add the name to userHashMap to update
            anyChanges = true
        }

        if(editTextMobileProfile.text.toString() != mUserDetails.mobile.toString()) {          //if the mobile changed
            userHashMap[Constants.MOBILE] = editTextMobileProfile.text.toString().toLong()    //then add the mobile to userHashMap to update
            anyChanges = true
        }
        if(anyChanges)
            FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    /**
     * open the gallery or ask the user for storage permission.
     */
    fun onClickUserImageProfile(view : View){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showImageChooser()
        }else{
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE)
        }
    }

    /**
     * open the gallery or give a toast message to ask the user for permission;
     * according to the user's response to the request for storage permission.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }else{
                Toast.makeText(this,"You just denied the permission for storage. You can allow it from the settings.",Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * open the gallery intent to pick an image for profile picture.
     */
    private fun showImageChooser(){
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)    //we want to get image data and result will be that data. therefore we call startActivityForResult
    }

    /**
     * when the user selects an image from gallery,
     * set the selected image as profile picture.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try {
                //https://github.com/bumptech/glide
                Glide.with(this)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(imageViewUserImageProfile)
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    /**
     * set the StorageReference and upload the image to FirebaseStorage.
     */
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null){      //if user chose an image to set profile picture.
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(mSelectedImageFileUri))
            //TODO: to select more specific storage reference add random number instead of currentTimeMillis()

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i("Firebase Image Url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this,exception.message.toString(),Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    /**
     * returns extension of input parameter
     */
    private fun getFileExtension(uri : Uri?): String? {
        return if(uri != null)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
        else
            null
    }

    /**
     * update the user details when click on update button
     */
    fun onClickButtonUpdateProfile(view: View){
        if(mSelectedImageFileUri != null){
            uploadUserImage()       //first, upload the user image to Firebase Storage then update user details on Firebase by calling updateUserProfileData() in uploadUserImage()
        }else {
            showProgressDialog(resources.getString(R.string.please_wait))
            updateUserProfileData()     //update user details on Firebase
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        finish()
    }
}