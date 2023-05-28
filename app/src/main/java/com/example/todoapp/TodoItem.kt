package com.example.todoapp

data class TodoItem (
    val title: String,
    val date: String,
    val time: String,
    val description: String,
    var isChecked: Boolean = false,
    val id: Int? = 0,
)