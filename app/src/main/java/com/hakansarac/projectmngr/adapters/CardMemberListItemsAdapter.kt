package com.hakansarac.projectmngr.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.models.Board
import com.hakansarac.projectmngr.models.SelectedMembers
import kotlinx.android.synthetic.main.item_card_selected_member.view.*

open class CardMemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card_selected_member,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1 && assignMembers) {
                holder.itemView.imageViewAddMember.visibility = View.VISIBLE
                holder.itemView.imageViewSelectedMemberImage.visibility = View.GONE
            } else {
                holder.itemView.imageViewAddMember.visibility = View.GONE
                holder.itemView.imageViewSelectedMemberImage.visibility = View.VISIBLE

                //https://github.com/bumptech/glide
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.imageViewSelectedMemberImage)
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
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
        fun onClick()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}