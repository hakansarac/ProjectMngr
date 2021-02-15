package com.hakansarac.projectmngr.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.activities.TaskListActivity
import com.hakansarac.projectmngr.models.Card
import com.hakansarac.projectmngr.models.SelectedMembers
import kotlinx.android.synthetic.main.item_card.view.*


open class CardListItemsAdapter(
        private val context: Context,
        private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_card,
                        parent,
                        false
                )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            if(model.labelColor.isNotEmpty()){
                holder.itemView.viewLabelColor.visibility = View.VISIBLE
                holder.itemView.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.viewLabelColor.visibility = View.GONE
            }
            holder.itemView.textViewCardName.text = model.name

            if((context as TaskListActivity).mAssignedMemberDetailList.size > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
                for(i in context.mAssignedMemberDetailList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMemberDetailList[i].id == j){
                            val selectedMembers = SelectedMembers(
                                    context.mAssignedMemberDetailList[i].id,
                                    context.mAssignedMemberDetailList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }

                if(selectedMembersList.size > 0){
                    if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        holder.itemView.recyclerViewCardSelectedMembers.visibility = View.GONE
                    }else{
                        holder.itemView.recyclerViewCardSelectedMembers.visibility = View.VISIBLE
                        holder.itemView.recyclerViewCardSelectedMembers.layoutManager = GridLayoutManager(context,4)
                        val adapter = CardMemberListItemsAdapter(context,selectedMembersList,false)
                        holder.itemView.recyclerViewCardSelectedMembers.adapter = adapter
                        adapter.setOnClickListener(object:CardMemberListItemsAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener != null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }else{
                    holder.itemView.recyclerViewCardSelectedMembers.visibility = View.GONE
                }
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}