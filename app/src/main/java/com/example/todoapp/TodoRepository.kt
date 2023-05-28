package com.example.todoapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class TodoRepository(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todoapp.db"
        private const val DATABASE_VERSION = 8
        private const val TABLE_NAME = "todo"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_IS_CHECKED = "isChecked"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "" +
                "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_TIME TEXT, " +
                "$COLUMN_IS_CHECKED BOOL," +
                "$COLUMN_IMAGE TEXT" +
                ")"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun addTodo(todoItem: TodoItem) {
        val contentValues = ContentValues()
        contentValues.put("title", todoItem.title)
        contentValues.put("description", todoItem.description)
        contentValues.put("date", todoItem.date)
        contentValues.put("time", todoItem.time)
        contentValues.put("isChecked", todoItem.isChecked)
        this.writableDatabase.insert(TABLE_NAME, null, contentValues)
    }

    @SuppressLint("Range")
    fun getTodos(): ArrayList<TodoItem> {
        val todos = ArrayList<TodoItem>()

        val cursor: Cursor = this.writableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("id")).toInt()
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                val isChecked = cursor.getString(cursor.getColumnIndex("isChecked")).toBoolean()
                todos.add(TodoItem(title, date, time, description, isChecked, id))
            } while (cursor.moveToNext())
        }

        cursor.close()
        this.writableDatabase.close()
        println(todos)
        return todos
    }

    fun updateTodo(id: Int, title: String, description: String) {
        val contentValues = ContentValues()
        contentValues.put("title", title)
        contentValues.put("description", description)
        writableDatabase.update(TABLE_NAME, contentValues, "id = $id", null)
        this.writableDatabase.close()
    }

    fun deleteTodo(id: Int) {
        writableDatabase.delete(TABLE_NAME, "id = $id", null)
        this.writableDatabase.close()
    }

    fun setIsCheckedToTrue(id: Int?) {
        val contentValues = ContentValues()
        contentValues.put("isChecked", "true")
        writableDatabase.update(TABLE_NAME, contentValues, "id = $id", null)
        this.writableDatabase.close()
    }
}
