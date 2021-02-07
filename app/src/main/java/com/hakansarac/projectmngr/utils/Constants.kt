package com.hakansarac.projectmngr.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.hakansarac.projectmngr.activities.MyProfileActivity

object Constants {
    const val USERS: String = "Users"
    const val BOARDS : String = "boards"
    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val MOBILE : String = "mobile"
    const val ASSIGNED_TO : String = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    /**
     * open the gallery intent to pick an image for profile picture.
     */
    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)    //we want to get image data and result will be that data. therefore we call startActivityForResult
    }

    /**
     * returns extension of input parameter
     */
    fun getFileExtension(activity: Activity, uri : Uri?): String? {
        return if(uri != null)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
        else
            null
    }
}