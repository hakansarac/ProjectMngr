package com.hakansarac.projectmngr.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        navigationView.setNavigationItemSelectedListener(this)
        FirestoreClass().signInUser(this)
    }

    /**
     * setting up the action bar
     * toggle icon added to open the drawer
     */
    private fun setupActionBar(){
        setSupportActionBar(toolbarMainActivity)
        toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * if drawer is open then close the drawer;
     * else if drawer is closed then open the drawer.
     */
    private fun toggleDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * added new functionality to onBackPressed;
     * if user presses the back button when the drawer is open,
     * close the drawer.
     */
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()      //this function is from BaseActivity
        }
    }

    /**
     * functionality added to navigation items.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //if user presses the My Profile button, then take the user to Profile page
            R.id.navMyProfile -> {
                val intent = Intent(this,MyProfileActivity::class.java)
                startActivity(intent)
            }
            //if user presses the SignOut button, then sign out and take the user to IntroActivity
            R.id.navSignOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                /**
                 * FLAG_ACTIVITY_CLEAR_TOP:
                 * If set, and the activity being launched is already running in the current task,
                 * then instead of launching a new instance of that activity,
                 * all of the other activities on top of it will be closed and
                 * this Intent will be delivered to the (now on top) old activity as a new Intent.
                 *
                 * This launch mode can be used to good effect in conjunction with FLAG_ACTIVITY_NEW_TASK:
                 * if used to start the root activity of a task,
                 * it will bring any currently running instance of that task to the foreground,
                 * and then clear it to its root state. This is especially useful, for example,
                 * when launching an activity from the notification manager.
                 */

                /**
                 * If set, this activity will become the start of a new task on this history stack.
                 * A task (from the activity that started it to the next task activity) defines an atomic group of activities that the user can move to.
                 * Tasks can be moved to the foreground and background;
                 * all of the activities inside of a particular task always remain in the same order.
                 */
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Get the image from firebase and show it as profile image by Glide(third party code)
     * In addition, add user name to Navigation User Detail
     */
    fun updateNavigationUserDetails(user : User){
        //https://github.com/bumptech/glide
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        textViewUserName.text = user.name
    }
}