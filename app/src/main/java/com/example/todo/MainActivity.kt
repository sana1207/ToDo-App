package com.example.todo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), TaskRowListener {

    lateinit var ndatabase: DatabaseReference
    var ntaskList: MutableList<Task>? = null
    lateinit var nadapter: TaskAdapter

    var ntaskListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadTaskList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {

            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        ndatabase = FirebaseDatabase.getInstance().reference
        ntaskList = mutableListOf<Task>()

        nadapter = TaskAdapter(this, ntaskList!!)
        listviewTask!!.setAdapter(nadapter)

        fab.setOnClickListener {
            showFooter()
        }

        btnAdd.setOnClickListener{
            addTask()
        }

        ndatabase.orderByKey().addValueEventListener(ntaskListener)

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    @SuppressLint("RestrictedApi")
    fun showFooter(){
        footer.visibility = View.VISIBLE
        fab.visibility = View.GONE
    }

    fun addTask(){


        val task = Task.create()


        task.taskDesc = txtNewTaskDesc.text.toString()
        task.done = false


        val newTask = ndatabase.child(Statics.FIREBASE_TASK).push()
        task.objectId = newTask.key


        newTask.setValue(task)

        @SuppressLint("RestrictedApi")
        footer.visibility = View.GONE
        footer.visibility = View.VISIBLE;

        txtNewTaskDesc.setText("")

        Toast.makeText(this, "New Task added to the List successfully" + task.objectId, Toast.LENGTH_SHORT).show()
    }

    private fun loadTaskList(dataSnapshot: DataSnapshot) {
        Log.d("MainActivity", "loadTaskList")

        val tasks = dataSnapshot.children.iterator()


        if (tasks.hasNext()) {

            ntaskList!!.clear()


            val listIndex = tasks.next()
            val itemsIterator = listIndex.children.iterator()


            while (itemsIterator.hasNext()) {


                val currentItem = itemsIterator.next()
                val task = Task.create()


              //  @Suppress("UNCHECKED_CAST")
                @Suppress("UNCHECKED_CAST")
                val map = currentItem.getValue() as HashMap<String, Any>


                task.objectId = currentItem.key
                task.done = map.get(("done")) as Boolean?
                task.taskDesc = map.get("taskDesc") as String?
                ntaskList!!.add(task)
            }
        }


        nadapter.notifyDataSetChanged()

    }

    override fun onTaskChange(objectId: String, isDone: Boolean) {
        val task = ndatabase.child(Statics.FIREBASE_TASK).child(objectId)

        task.child("done").setValue(isDone)
    }


    override fun onTaskDelete(objectId: String) {
        val task = ndatabase.child(Statics.FIREBASE_TASK).child(objectId)
        task.removeValue()
    }

}
