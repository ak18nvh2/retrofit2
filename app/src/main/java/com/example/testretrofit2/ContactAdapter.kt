package com.example.testretrofit2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testretrofit2.models.Contact


class ContactAdapter(var mContext : Context, var iRecyclerViewWithHomeActivity: IRecyclerViewWithHomeActivity) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    private var list : ArrayList<Contact>?= ArrayList()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val id : TextView = itemView.findViewById(R.id.tv_Id)
        val name : TextView = itemView.findViewById(R.id.tv_Name)
    }
    fun setList(list : ArrayList<Contact>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_employee, parent, false))
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.id.text = "ID: "+ list?.get(position)?.contactId
        holder.name.text = "Name: "+list?.get(position)?.lastName  + list?.get(position)?.firstName
        holder.itemView.setOnClickListener(){
            iRecyclerViewWithHomeActivity.doSomeThingOnClick(list?.get(position)!!)
        }
        holder.itemView.setOnLongClickListener(){
            iRecyclerViewWithHomeActivity.doSomeThingOnLongClick(list?.get(position)!!)
            return@setOnLongClickListener true
        }
    }
    interface IRecyclerViewWithHomeActivity{
        fun doSomeThingOnClick(contact: Contact)
        fun doSomeThingOnLongClick(contact: Contact)
    }
}