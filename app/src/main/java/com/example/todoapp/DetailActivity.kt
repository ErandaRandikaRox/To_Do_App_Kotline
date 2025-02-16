package com.example.todoapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * DetailActivity is responsible for displaying the details of a selected to-do item.
 * It retrieves the title and description from the intent and displays them in the UI.
 */
class DetailActivity : AppCompatActivity() {

    /**
     * Called when the activity is first created.
     * Initializes the UI and sets the text views with the received data.
     *
     * @param savedInstanceState The saved instance state of the activity, if available.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Retrieve title and description from intent extras
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")

        // Set the retrieved data to the respective TextViews
        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<TextView>(R.id.detailDescription).text = description
    }
}
