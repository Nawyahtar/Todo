package com.example.myapplication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val toDoItemClickListenerDelete: ToDoItemClickListenerDelete,private val toDoItemClickListenerUpdate: ToDoItemClickListenerUpdate) :androidx.recyclerview.widget.ListAdapter<ToDo,RecyclerAdapter.ToDoViewHolder>(
    object :DiffUtil.ItemCallback<ToDo>(){
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
           return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
           return oldItem==newItem
        }

    }
){
    class ToDoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tvTask=itemView.findViewById<TextView>(R.id.tvTask)
        val ivRemove=itemView.findViewById<ImageView>(R.id.ivRemove)
        val ivUpdate=itemView.findViewById<ImageView>(R.id.ivUpdate)

    }
    interface ToDoItemClickListenerDelete{
        fun onDelete(toDo: ToDo)
    }
    interface ToDoItemClickListenerUpdate{
        fun onUpdate(toDo: ToDo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val viewHolder=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        val holder=ToDoViewHolder(viewHolder)
        holder.ivRemove.setOnClickListener{
            val position=holder.adapterPosition
            if (position!=RecyclerView.NO_POSITION){
                val itemAtIndex=getItem(position)
                toDoItemClickListenerDelete.onDelete(itemAtIndex)
            }
        }
        holder.ivUpdate.setOnClickListener{
            val position=holder.adapterPosition
            if (position!=RecyclerView.NO_POSITION){
                val itemAtIndex=getItem(position)
                toDoItemClickListenerUpdate.onUpdate(itemAtIndex)
            }
        }

        return holder
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
       val itemAtIndex=getItem(position)
        holder.tvTask.text="${itemAtIndex.position}. ${itemAtIndex.todo}"
    }
}