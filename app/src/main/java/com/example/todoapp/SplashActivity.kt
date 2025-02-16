package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 * SplashActivity is the launching screen of the application.
 * It displays a splash screen for 2 seconds before transitioning to MainActivity.
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Called when the activity is first created.
     * Displays the splash screen and schedules a transition to MainActivity after a delay.
     *
     * @param savedInstanceState The saved instance state of the activity, if available.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 2 seconds and then launch MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close SplashActivity so the user can't go back to it
        }, 2000) // 2000 milliseconds (2 seconds)
    }
}
