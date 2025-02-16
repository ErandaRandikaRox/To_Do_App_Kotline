package com.example.todoapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Adapter class for managing the list of To-Do items in a RecyclerView.
 * @param todoList List of ToDoItem objects.
 * @param dbHelper Instance of DatabaseHelper to handle database operations.
 * @param onItemClick Callback function to handle item click events.
 */
class TodoAdapter(
    private var todoList: MutableList<ToDoItem>,
    private val dbHelper: DatabaseHelper,
    private val onItemClick: (ToDoItem) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    /**
     * ViewHolder class to hold and manage the UI components of each To-Do item.
     */
    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkboxComplete)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val description: TextView = view.findViewById(R.id.tvDescription)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList[position]

        holder.title.text = item.title
        holder.description.text = item.description

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = item.isCompleted
        updateStrikeThrough(holder, item.isCompleted)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedItem = item.copy(isCompleted = isChecked)
            todoList[position] = updatedItem
            updateStrikeThrough(holder, isChecked)
            dbHelper.updateTaskStatus(updatedItem.id, isChecked)
            notifyItemChanged(position)
        }

        holder.btnDelete.setOnClickListener {
            dbHelper.deleteTask(item.id)
            todoList.removeAt(position)
            notifyItemRemoved(position)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = todoList.size

    /**
     * Updates the UI to apply or remove strikethrough effect based on completion status.
     */
    private fun updateStrikeThrough(holder: TodoViewHolder, isChecked: Boolean) {
        if (isChecked) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.description.paintFlags = holder.description.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.description.paintFlags = holder.description.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}

/**
 * Database helper class for managing SQLite database operations.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TodoDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IS_COMPLETED = "isCompleted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_IS_COMPLETED INTEGER
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * Inserts a new To-Do item into the database.
     */
    fun insertTodo(todo: ToDoItem): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_DESCRIPTION, todo.description)
            put(COLUMN_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    /**
     * Retrieves all To-Do items from the database.
     */
    fun getAllTodos(): List<ToDoItem> {
        val todoList = mutableListOf<ToDoItem>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val task = ToDoItem(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
            )
            todoList.add(task)
        }
        cursor.close()
        db.close()
        return todoList
    }

    /**
     * Updates the completion status of a specific To-Do item in the database.
     */
    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(taskId.toString()))
        db.close()
    }

    /**
     * Deletes a specific To-Do item from the database.
     */
    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(taskId.toString()))
        db.close()
    }
}

/**
 * Data class representing a To-Do item.
 */
data class ToDoItem(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
