/**
 * Data class representing a ToDo item.
 *
 * @property id The unique identifier for the task (default is 0 for new tasks).
 * @property title The title of the task.
 * @property description The description of the task.
 * @property isCompleted Indicates whether the task is completed (default is false).
 */
data class ToDoItem(
    val id: Int = 0, // Default ID for new tasks
    val title: String,
    val description: String,
    var isCompleted: Boolean = false // Default to false
)
