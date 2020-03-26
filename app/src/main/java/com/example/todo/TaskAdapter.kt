package com.example.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView


class TaskAdapter(context: Context, taskList: MutableList<Task>) : BaseAdapter() {

    private val ninflater: LayoutInflater = LayoutInflater.from(context)
    private var ntaskList = taskList
    private var nrowListener: TaskRowListener = context as TaskRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val objectId: String = ntaskList.get(position).objectId as String
        val itemText: String = ntaskList.get(position).taskDesc as String
        val done: Boolean = ntaskList.get(position).done as Boolean

        val view: View
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = ninflater.inflate(R.layout.task_rows, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.desc.text = itemText
        listRowHolder.done.isChecked = done
        listRowHolder.done.setOnClickListener {
            nrowListener.onTaskChange(objectId, !done) }

        listRowHolder.remove.setOnClickListener {
            nrowListener.onTaskDelete(objectId) }


        return view
    }

    override fun getItem(index: Int): Any {
        return ntaskList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return ntaskList.size
    }

    private class ListRowHolder(row: View?) {
        val desc: TextView = row!!.findViewById(R.id.txtTaskDesc) as TextView
        val done: CheckBox = row!!.findViewById(R.id.chkDone) as CheckBox
        val remove: ImageButton = row!!.findViewById(R.id.btnRemove) as ImageButton
    }
}