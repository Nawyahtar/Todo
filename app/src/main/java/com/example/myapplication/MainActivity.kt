package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),RecyclerAdapter.ToDoItemClickListenerDelete,RecyclerAdapter.ToDoItemClickListenerUpdate {
    private lateinit var binding: ActivityMainBinding
    private val recyclerAdapter = RecyclerAdapter(this,this)
    private lateinit var toDoListNode: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Daily Task")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.rvRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }
        toDoListNode = Firebase.database.reference.child("todolist")
        toDoListNode.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList: List<ToDo> = snapshot.children.mapIndexed { index, dataSnapshot ->
                    val task=dataSnapshot.child("task").getValue<String>().orEmpty()
                    val id=dataSnapshot.key!!

                    ToDo(
                        position = index + 1,
                        id=id,
                        todo = task


                    )


                }
                recyclerAdapter.submitList(newList)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.addmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                val editText = EditText(this)
                val alertDialog = AlertDialog.Builder(this)
                    .setView(editText)
                    .setTitle("Enter your new task")
                    .setPositiveButton("Ok") { dialog, _ ->
                        val todo = editText.text.toString()

                        if (todo.isNotEmpty()) {
                            sendData(todo)
                        }else{
                            Toast.makeText(this, "Please enter the task field", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()

                    }.create()
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendData(todo: String) {
        toDoListNode.push().apply {
            child("task").setValue(todo)
        }

    }

    override fun onDelete(toDo: ToDo) {
        toDoListNode.child(toDo.id).removeValue()
    }

    override fun onUpdate(toDo: ToDo) {
        val editText = EditText(this)
        editText.setText(toDo.todo)
        val alertDialog = AlertDialog.Builder(this)
            .setView(editText)
            .setTitle("Enter your new  task's name")
            .setPositiveButton("Ok") { dialog, _ ->
                val todoTask = editText.text.toString()

                if (todoTask.isNotEmpty()) {
                    updateTask(toDo.id,todoTask)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this, "Please enter the task field", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()

            }.create()
        alertDialog.show()
    }

    private fun updateTask(id: String, todoTask: String) {
        val data= mapOf(
            "task" to todoTask
        )
        val childUpdates = hashMapOf<String, Any>(
            "/todolist/${id}" to data,

            )
        val rootReference = Firebase.database.reference
        rootReference.updateChildren(childUpdates)
        Toast.makeText(this,"Update Successful",Toast.LENGTH_SHORT).show()
    }


}