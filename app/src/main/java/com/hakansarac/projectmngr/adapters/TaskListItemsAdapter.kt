package com.hakansarac.projectmngr.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.hakansarac.projectmngr.R
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
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)
}