package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpPost
import java.time.LocalDate
import java.util.*
import com.github.kittinunf.result.Result
import com.google.api.AnnotationsProto.http
import com.google.gson.Gson
import org.json.JSONArray

// 1, 15, 20, 30

// In AddTask change "Date" to "Time" ✓
// Write repository class ✓
// Add / Remove / Update tasks ✓
// Mark task as done ✓
// validation ✓

val API_URL = "http://192.168.0.135:8080/"

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var todoAdapter: TodoItemAdapter
    private lateinit var todoRepository: TodoRepository
    val selectedDate = Calendar.getInstance()

    private lateinit var sensorManager: SensorManager
    private lateinit var tempSensor: Sensor
    private lateinit var tvTemperature: TextView
    private var isTempSensorAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoAdapter = TodoItemAdapter(mutableListOf(), ::editTodoHandler, ::setIsCheckedHandler)
        todoRepository = TodoRepository(this)

        val newTaskButton: Button = findViewById(R.id.bNewTask)
        val backupButton: Button = findViewById(R.id.bBackup)
        val calendar: CalendarView = findViewById(R.id.calendarView)
        val rvTodoItems: RecyclerView = findViewById(R.id.rvTodoItems)
        val tvBackupResult: TextView = findViewById(R.id.tvBackupResult)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)


        var todos = todoRepository.getTodos().filter { todo -> todo.date == dateFormat.format(selectedDate.time) }
        for (todo in todos) {
            todoAdapter.addTodo(todo)
        }

        newTaskButton.setOnClickListener {
            println("NEW TASK CLICKED")
            val intent = Intent(this, AddTask::class.java)
            intent.putExtra("calendar-date", dateFormat.format(selectedDate.time))
            startActivity(intent)
        }

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            todoAdapter.deleteAllTodos()
            selectedDate.set(year, month, dayOfMonth)
            var todos = todoRepository.getTodos().filter { todo -> todo.date == dateFormat.format(selectedDate.time) }
            for (todo in todos) {
                todoAdapter.addTodo(todo)
            }
        }

        // Temperature sensor handling
        tvTemperature = findViewById(R.id.tvTemperature)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) !== null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            isTempSensorAvailable = true
        } else {
            tvTemperature.text = "N/A"
        }

        // Backup handling
        backupButton.setOnClickListener {
            Thread {
                val url = API_URL
                val todos = todoRepository.getTodos()

                val requestData = JSONArray(todos).toString()
                val serialized = Gson().toJson(todos)
                println(requestData)
                val request = url.httpPost()
                    .header("Content-Type" to "application/json")
                    .body(serialized)

                val (_, response, result) = request.responseString()
                println(result.toString())
                when (result) {
                    is Result.Success -> {
                        val data = result.get()
                        tvBackupResult.text = "Backup has been completed"
                        println("Response: $data")
                    }
                    is Result.Failure -> {
                        val error = result.getException()
                        tvBackupResult.text = "Backup error"
                        println("Error: ${error.message}")
                    }
                }

                runOnUiThread {
                    //Update UI
                }
            }.start()

        }
    }

    @Override
    override fun onSensorChanged(event: SensorEvent?) {
        tvTemperature.text = ((event!!.values[0].toString()) + "°C") as String
    }

    @Override
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @Override
    override fun onResume() {
        super.onResume()
        if (isTempSensorAvailable) {
            sensorManager.registerListener(this,tempSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    @Override
    override fun onPause() {
        super.onPause()
        if (isTempSensorAvailable) {
            sensorManager.unregisterListener(this)
        }
    }

    private fun editTodoHandler(todo: TodoItem) {
        val intent = Intent(this, EditTask::class.java)
        println("editTodoHandler" + todo)
        intent.putExtra("id", todo.id)
        intent.putExtra("title", todo.title)
        intent.putExtra("description", todo.description)
        println("ASDASDASDASDASDASDASD")
        startActivity(intent)
    }

    private fun setIsCheckedHandler(id: Int?) {
        todoRepository.setIsCheckedToTrue(id)
    }
}