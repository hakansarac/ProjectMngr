package com.hakansarac.projectmngr.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
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
            //if user closes the card view,
            //hide the card view and show Add Task List text view button
            holder.itemView.imageButtonCloseListName.setOnClickListener {
                holder.itemView.textViewAddTaskList.visibility = View.VISIBLE
                holder.itemView.cardViewAddTaskListName.visibility = View.GONE
            }
            //if user creates new task,
            //create entry in DB and display the task list
            holder.itemView.imageButtonDoneListName.setOnClickListener {
                val listName = holder.itemView.editTextTaskListName.text.toString()
                if(listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                }else{
                        Toast.makeText(context,"Please enter a list name.",Toast.LENGTH_SHORT).show()
                }
            }
            //if user wants to edit list name,
            //open the card view to edit task list name.
            holder.itemView.imageButtonEditListName.setOnClickListener {
                holder.itemView.editTextEditTaskListName.setText(model.title)
                holder.itemView.linearLayoutTitleView.visibility = View.GONE
                holder.itemView.cardViewEditTaskListName.visibility = View.VISIBLE
            }
            //if user cancels edit,
            //close the card view to edit task list name.
            holder.itemView.imageButtonCloseEditableView.setOnClickListener {
                holder.itemView.linearLayoutTitleView.visibility = View.VISIBLE
                holder.itemView.cardViewEditTaskListName.visibility = View.GONE
            }
            //if the user changes the list name,
            //update the task and task list
            holder.itemView.imageButtonDoneEditListName.setOnClickListener {
                val listName = holder.itemView.editTextEditTaskListName.text.toString()
                if(listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(position, listName, model)
                    }
                }else{
                    Toast.makeText(context,"Please enter a list name.",Toast.LENGTH_SHORT).show()
                }
            }
            //if the user deletes a task,
            //delete it and update the task list
            holder.itemView.imageButtonDeleteList.setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }
            //if the user presses add card button,
            //hide add card text and show add card cardView
            holder.itemView.textViewAddCard.setOnClickListener {
                holder.itemView.textViewAddCard.visibility = View.GONE
                holder.itemView.cardViewAddCard.visibility = View.VISIBLE
            }
            //if the user presses cancel add card button,
            //show add card text and hide add card cardView
            holder.itemView.imageButtonCloseCardName.setOnClickListener {
                holder.itemView.textViewAddCard.visibility = View.VISIBLE
                holder.itemView.cardViewAddCard.visibility = View.GONE
            }
            //if user creates new card,
            //create entry in DB and display the card
            holder.itemView.imageButtonDoneCardName.setOnClickListener {
                val cardName = holder.itemView.editTextCardName.text.toString()
                if(cardName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addCardToTaskList(position,cardName)
                    }
                }else{
                    Toast.makeText(context,"Please enter a card name.",Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.recyclerViewCardList.layoutManager = LinearLayoutManager(context)
            holder.itemView.recyclerViewCardList.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context,model.cards)
            holder.itemView.recyclerViewCardList.adapter = adapter
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()
            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()
    }


    private fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)
}