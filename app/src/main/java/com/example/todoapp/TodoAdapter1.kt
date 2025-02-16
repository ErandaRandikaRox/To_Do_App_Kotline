package com.example.todoapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ItemTodoBinding

/**
 * Adapter class for handling a list of ToDo items in a RecyclerView.
 *
 * @param todoList The list of ToDo items to be displayed.
 * @param dbHelper The database helper for performing CRUD operations.
 * @param onTaskClick Callback function to handle item clicks.
 */
class TodoAdapter1(
    private var todoList: MutableList<ToDoItem>,
    private val dbHelper: DatabaseHelper,
    private val onTaskClick: (ToDoItem) -> Unit
) : RecyclerView.Adapter<TodoAdapter1.TodoViewHolder>() {

    /**
     * ViewHolder class for managing individual ToDo item views.
     *
     * @param binding The view binding for an item layout.
     */
    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a ToDo item to the ViewHolder.
         *
         * @param todo The ToDo item to bind.
         * @param position The position of the item in the list.
         */
        fun bind(todo: ToDoItem, position: Int) {
            binding.tvTitle.text = todo.title
            binding.tvDescription.text = todo.description

            // Remove previous listener to prevent unintended triggers
            binding.checkboxComplete.setOnCheckedChangeListener(null)
            binding.checkboxComplete.isChecked = todo.isCompleted

            // Apply strike-through effect if completed
            updateStrikeThrough(todo.isCompleted)

            // Handle checkbox change event
            binding.checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
                todoList[position] = todo.copy(isCompleted = isChecked)
                dbHelper.updateTaskStatus(todo.id, isChecked)
                updateStrikeThrough(isChecked)
                notifyItemChanged(position)
            }

            // Handle task deletion
            binding.btnDelete.setOnClickListener {
                dbHelper.deleteTask(todo.id)
                todoList.removeAt(position)
                notifyItemRemoved(position)
            }

            // Handle item click event
            binding.root.setOnClickListener { onTaskClick(todo) }
        }

        /**
         * Updates the text appearance based on the completion status.
         *
         * @param isChecked True if the task is completed, false otherwise.
         */
        private fun updateStrikeThrough(isChecked: Boolean) {
            val flag = if (isChecked) Paint.STRIKE_THRU_TEXT_FLAG else 0
            binding.tvTitle.paintFlags = flag
            binding.tvDescription.paintFlags = flag
        }
    }

    /**
     * Inflates the item layout and returns a ViewHolder.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of view.
     * @return A new instance of TodoViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    /**
     * Binds data to the ViewHolder at a specific position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item.
     */
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todoList[position], position)
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The size of the ToDo list.
     */
    override fun getItemCount(): Int = todoList.size

    /**
     * Adds a new task to the list and updates the RecyclerView.
     *
     * @param task The new task to be added.
     */
    fun addTask(task: ToDoItem) {
        todoList.add(task)
        notifyItemInserted(todoList.size - 1)
    }

    /**
     * Removes a task from the list at a given position.
     *
     * @param position The position of the task to remove.
     */
    fun removeTask(position: Int) {
        if (position in todoList.indices) {
            dbHelper.deleteTask(todoList[position].id)
            todoList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Updates an existing task at a given position.
     *
     * @param position The position of the task to update.
     * @param newTask The new task data to replace the existing one.
     */
    fun updateTask(position: Int, newTask: ToDoItem) {
        if (position in todoList.indices) {
            todoList[position] = newTask
            notifyItemChanged(position)
        }
    }
}
