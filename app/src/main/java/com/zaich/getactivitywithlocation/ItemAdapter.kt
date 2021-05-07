package com.zaich.getactivitywithlocation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter (val context: Context, val items : ArrayList<AbsenModel>)
    :RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
        class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val llMain = view.llMain
            val tvId = view.tvId
            val tvName = view.tvName
            val tvTime = view.tvTime
            val tvAddress = view.tvAddress
            val ivDelete = view.ivDelete

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_row,parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = items.get(position)
        holder.tvId.text = items.id.toString()
        holder.tvName.text = items.name
        holder.tvTime.text = items.waktu
        holder.tvAddress.text = items.lokasi

        if (position % 2 == 0 ){
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.orangemuda))
        }else{
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.DarkOrange))
        }
        holder.ivDelete.setOnClickListener {
            if(context is history1){
                context.deleteRecordAlertDialog(items)
            }
        }
    }



}