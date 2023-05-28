package com.example.todoapp;

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoItemAdapter(
        private val todos: MutableList<TodoItem>,
        private val editTodoHandler: (todo: TodoItem) -> Unit,
        private val setIsCheckedHandler: (id: Int?) -> Unit
) : RecyclerView.Adapter<TodoItemAdapter.TodoViewHolder>() {

        class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val tvTodoTitle: TextView = itemView.findViewById(R.id.tvTodoTitle)
                val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        }

        fun addTodo(todo: TodoItem) {
                todos.add(todo)
                notifyItemInserted(todos.size - 1)
        }

        fun deleteDoneTodos() {
                todos.removeAll { todo ->
                        todo.isChecked
                }
                notifyDataSetChanged()
        }

        fun deleteAllTodos() {
                todos.clear()
                notifyDataSetChanged()
        }

        private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean, id: Int?) {
                if(isChecked) {
                        tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
                } else {
                        tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
                return TodoViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.todo_item,
                                parent,
                                false
                        )
                )
        }

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
                val curTodo = todos[position]
                holder.apply {
                        tvTodoTitle.text = curTodo.time + ": " + curTodo.title
                        cbDone.isChecked = curTodo.isChecked
                        if (curTodo.isChecked) {
                                cbDone.isEnabled = false;
                        }
                        toggleStrikeThrough(tvTodoTitle, curTodo.isChecked, curTodo.id)

                        cbDone.setOnCheckedChangeListener { _, isChecked ->
                                if (!curTodo.isChecked) {
                                        toggleStrikeThrough(tvTodoTitle, isChecked, curTodo.id)
                                        curTodo.isChecked = !curTodo.isChecked
                                        setIsCheckedHandler(curTodo.id)
                                }
                        }

                        tvTodoTitle.setOnClickListener {
                                editTodoHandler(curTodo)
                        }
                }
        }

        override fun getItemCount(): Int {
                return todos.size
        }
}
