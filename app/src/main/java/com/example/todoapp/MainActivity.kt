package com.example.todoapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding

/**
 * MainActivity is responsible for displaying and managing the list of to-do tasks.
 * It provides functionality for adding, viewing, and refreshing tasks.
 */
class MainActivity : AppCompatActivity() {

    // View binding for accessing UI elements
    private lateinit var binding: ActivityMainBinding

    // Database helper for managing CRUD operations
    private lateinit var dbHelper: DatabaseHelper

    // Adapter for managing RecyclerView items
    private lateinit var adapter: TodoAdapter

    // List to store to-do items
    private var todoList = mutableListOf<ToDoItem>()

    /**
     * Called when the activity is first created.
     * Initializes the UI components, database, and RecyclerView adapter.
     *
     * @param savedInstanceState The saved instance state of the activity, if available.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper and fetch all tasks
        dbHelper = DatabaseHelper(this)
        todoList = dbHelper.getAllTodos().toMutableList()

        // Initialize RecyclerView adapter with click listener for task details
        adapter = TodoAdapter(todoList, dbHelper) { selectedTask ->
            showTaskDetailsDialog(selectedTask)
        }

        // Set up RecyclerView with layout manager and adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Set click listener for the Floating Action Button to add a new task
        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    /**
     * Called when the activity is resumed.
     * Refreshes the task list to reflect any changes.
     */
    override fun onResume() {
        super.onResume()
        refreshList()
    }

    /**
     * Displays a dialog to add a new to-do task.
     * The user can enter a title and description before saving the task.
     */
    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Task")

        // Inflate the custom layout for the dialog
        val view = layoutInflater.inflate(R.layout.dialog_add_task, null)
        builder.setView(view)

        // Initialize input fields and buttons
        val etTitle = view.findViewById<EditText>(R.id.etTaskTitle)
        val etDescription = view.findViewById<EditText>(R.id.etTaskDescription)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        val dialog = builder.create()
        dialog.show()

        // Handle save button click
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            // Ensure title and description are not empty
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Title and Description cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert new task into database and refresh the list
            dbHelper.insertTodo(ToDoItem(title = title, description = description))
            refreshList()
            dialog.dismiss()
        }

        // Handle cancel button click
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    /**
     * Refreshes the task list by retrieving updated data from the database.
     */
    private fun refreshList() {
        todoList.clear()
        todoList.addAll(dbHelper.getAllTodos())
        adapter.notifyDataSetChanged()
    }

    /**
     * Displays a dialog showing the details of the selected task.
     *
     * @param task The selected to-do item.
     */
    private fun showTaskDetailsDialog(task: ToDoItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Task Details")
        builder.setMessage("Title: ${task.title}\n\nDescription: ${task.description}")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
