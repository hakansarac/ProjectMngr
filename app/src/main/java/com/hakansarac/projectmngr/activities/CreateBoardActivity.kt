package com.hakansarac.projectmngr.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var mSelectedImageFileUri : Uri? = null
    private lateinit var mUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)!!   //we get username by this way, because we want to have as few request as possible.
        }

        setupActionBar()
    }

    /**
     * setting up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * open the gallery or give a toast message to ask the user for permission;
     * according to the user's response to the request for storage permission.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"You just denied the permission for storage. You can allow it from the settings.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * when the user selects an image from gallery,
     * set the selected image as board picture.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try {
                //https://github.com/bumptech/glide
                Glide.with(this)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(imageViewBoardImage)
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    /**
     * open the gallery or ask the user for storage permission.
     */
    fun onClickCreateBoardImage(view : View){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this)
        }else{
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }
}