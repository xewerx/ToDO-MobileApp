package com.example.todoapp

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import android.Manifest
import com.google.android.gms.tasks.Task
import com.itextpdf.text.Document

class EditTask : AppCompatActivity() {

    private lateinit var todoRepository: TodoRepository
    private val STORAGE_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        val cancelButton: Button = findViewById(R.id.bEditTaskCancel)
        val saveButton: Button = findViewById(R.id.bEditTaskSave)
        val deleteButton: Button = findViewById(R.id.bEditTaskDelete)
        val savePdfButton: Button = findViewById(R.id.bSavePdf)
        val tvError: TextView = findViewById(R.id.tvError)
        val titleInput: TextView = findViewById(R.id.etEditTaskTitle)
        val descriptionInput: TextView = findViewById(R.id.etEditTaskDescription)

        val Intent = getIntent()
        val mainActivity = Intent(this, MainActivity::class.java)

        val id: Int = Intent.getIntExtra("id", 0)
        val title: String = Intent.getStringExtra("title") as String;
        val description: String = Intent.getStringExtra("description") as String;

        titleInput.text = title
        descriptionInput.text = description

        cancelButton.setOnClickListener {
            startActivity(mainActivity)
        }

        saveButton.setOnClickListener {
            if (titleInput.text.toString() == "") {
                tvError.text = "Title can not be empty"
                return@setOnClickListener
            }

            editTodo(
                id,
                titleInput.text.toString(),
                descriptionInput.text.toString(),
            )
            startActivity(mainActivity)
        }

        deleteButton.setOnClickListener {
            deleteTodo(id)
            startActivity(mainActivity)
        }

        // Generate pdf handling
        savePdfButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Sprawdzanie uprawnień do zapisu na pamięci zewnętrznej dla Android 6.0 i nowszych
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Jeżeli uprawnienia nie są przyznane, wywołaj żądanie uprawnień
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                    generatePDF(title, description)
                } else {
                    // Uprawnienia są już przyznane
                    generatePDF(title, description)
                }
            } else {
                // Dla starszych wersji Androida generowanie PDF nie wymaga uprawnień
                generatePDF(title, description)
            }
        }
    }

    private fun editTodo(id: Int, title: String, description: String) {
        todoRepository = TodoRepository(this)
        todoRepository.updateTodo(id, title, description)
    }

    private fun deleteTodo(id: Int) {
        todoRepository = TodoRepository(this)
        todoRepository.deleteTodo(id)
    }

    private fun generatePDF(title: String, description: String) {
        val document = Document()

        try {
            val filePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + title + ".pdf"

            PdfWriter.getInstance(document, FileOutputStream(filePath))

            document.open()

            document.add(Paragraph("Task title: " + title))
            document.add(Paragraph("Description: " + description))

            document.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}