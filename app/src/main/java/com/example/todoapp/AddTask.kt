package com.example.todoapp

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*

class AddTask : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var todoRepository: TodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val Intent = getIntent()
        val date: String = Intent.getStringExtra("calendar-date") as String;
        val cancelButton: Button = findViewById(R.id.bAddTaskCancel)
        val saveButton: Button = findViewById(R.id.bAddTaskSave)
        val titleInput: TextView = findViewById(R.id.etTitle)
        val tvError: TextView = findViewById(R.id.tvError)
        val descriptionInput: TextView = findViewById(R.id.etDescription)
        timeTextView = findViewById(R.id.timeTextView)
        val intent = Intent(this, MainActivity::class.java)

        cancelButton.setOnClickListener {
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            if (titleInput.text.toString() == "") {
                tvError.text = "Title can not be empty"
                return@setOnClickListener
            }

            if (timeTextView.text.toString() == "") {
                tvError.text = "Time can not be empty"
                return@setOnClickListener
            }

            addTodo(TodoItem(
                titleInput.text.toString(),
                date,
                timeTextView.text.toString(),
                descriptionInput.text.toString(),
                false
            ))
            startActivity(intent)
        }

        val pickTimeButton: Button = findViewById(R.id.pickTimeButton)
        pickTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

    }

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeTextView.text = selectedTime
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun addTodo(todo: TodoItem) {
        todoRepository = TodoRepository(this)
        todoRepository.addTodo(todo)
    }
}