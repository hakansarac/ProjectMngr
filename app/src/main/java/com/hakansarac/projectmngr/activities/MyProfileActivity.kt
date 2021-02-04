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
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri : Uri? = null
    private var mProfileImageURL : String = ""

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
     * upload user profile image
     */
    fun onClickButtonUpdateProfile(view: View){
        if(mSelectedImageFileUri != null){
            uploadUserImage()
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
                    hideProgressDialog()
                    //TODO update user profile data
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
}