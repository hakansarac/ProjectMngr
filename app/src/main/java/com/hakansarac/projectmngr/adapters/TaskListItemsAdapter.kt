package com.hakansarac.projectmngr.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.activities.TaskListActivity
import com.hakansarac.projectmngr.models.Task
import kotlinx.android.synthetic.main.item_task.view.*

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent,false)
        val layoutParams = LinearLayout.LayoutParams((parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT) // parameters: width,height
        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp().toPx()),0)  //parameters: left,top,right,bottom
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size-1){
                //add task text should be visible if we have no entries in our list and the other stuff should not be visible
                holder.itemView.textViewAddTaskList.visibility = View.VISIBLE
                holder.itemView.linearLayoutTaskItem.visibility = View.GONE
            }else{
                holder.itemView.textViewAddTaskList.visibility = View.GONE
                holder.itemView.linearLayoutTaskItem.visibility = View.VISIBLE
            }

            holder.itemView.textViewTaskListTitle.text = model.title

            //if user clicks on Add Task List text view button,
            //hide the button and show add task list name card view
            holder.itemView.textViewAddTaskList.setOnClickListener {
                holder.itemView.textViewAddTaskList.visibility = View.GONE
                holder.itemView.cardViewAddTaskListName.visibility = View.VISIBLE
            }
            //if user close the card view,
            //hide the card view and show Add Task List text view button
            holder.itemView.imageButtonCloseListName.setOnClickListener {
                holder.itemView.textViewAddTaskList.visibility = View.VISIBLE
                holder.itemView.cardViewAddTaskListName.visibility = View.GONE
            }
            //if user create new task,
            //create entry in DB and display the task list
            holder.itemView.imageButtonDoneListName.setOnClickListener {
                val listName = holder.itemView.editTextTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }else{
                        Toast.makeText(context,"Please enter list name.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)
}