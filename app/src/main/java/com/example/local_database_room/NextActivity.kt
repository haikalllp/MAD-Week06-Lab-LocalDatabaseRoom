package com.example.local_database_room

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class NextActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var editContainer: LinearLayout
    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var updateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private lateinit var dao: StudentDAO
    private var currentStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next)

        backButton = findViewById(R.id.backButton)
        editContainer = findViewById(R.id.editContainer)
        editName = findViewById(R.id.editName)
        editAge = findViewById(R.id.editAge)
        updateButton = findViewById(R.id.updateButton)
        cancelButton = findViewById(R.id.cancelButton)
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView)
        dao = AppDatabase.getDatabase(this).studentDao()

        // Setup RecyclerView
        adapter = StudentAdapter(emptyList()) { student ->
            currentStudent = student
            showEditForm()
        }
        studentsRecyclerView.adapter = adapter
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // 🔹 Back button closes this activity
        backButton.setOnClickListener {
            finish()
        }

        // 🔹 Update button
        updateButton.setOnClickListener {
            updateStudent()
        }

        // 🔹 Cancel button
        cancelButton.setOnClickListener {
            hideEditForm()
        }

        // Load students
        loadStudents()
    }

    private fun showEditForm() {
        currentStudent?.let { student ->
            editName.setText(student.name)
            editAge.setText(student.age.toString())
            editContainer.visibility = LinearLayout.VISIBLE
        }
    }

    private fun hideEditForm() {
        editContainer.visibility = LinearLayout.GONE
        currentStudent = null
    }

    private fun updateStudent() {
        val name = editName.text.toString().trim()
        val ageText = editAge.text.toString().trim()

        if (name.isNotEmpty() && ageText.isNotEmpty()) {
            try {
                val age = ageText.toInt()
                if (age > 0 && currentStudent != null) {
                    val updatedStudent = currentStudent!!.copy(name = name, age = age)
                    lifecycleScope.launch {
                        dao.update(updatedStudent)
                        hideEditForm()
                        loadStudents() // Refresh the list
                        Toast.makeText(this@NextActivity, "Student updated", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val students = dao.getAllStudents()
            adapter.updateStudents(students) // pass the students data to the adapter
        }
    }
}