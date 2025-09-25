package com.example.local_database_room

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class NextActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var backButton: Button
    private lateinit var dao: StudentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next)
        container = findViewById(R.id.containerStudents)
        backButton = findViewById(R.id.backButton)
        dao = AppDatabase.getDatabase(this).studentDao()

        // 🔹 Back button closes this activity
        backButton.setOnClickListener {
            finish()
        }

        // 🔹 Load students and add them to the container
        lifecycleScope.launch {
            val students = dao.getAllStudents()
            container.removeAllViews()
            students.forEach { student ->
                val tv = TextView(this@NextActivity).apply {
                    text = "• ${student.name} (ID: ${student.id})"
                    textSize = 18f
                    setPadding(8)
                }
                container.addView(tv)
            }
        }
    }
}