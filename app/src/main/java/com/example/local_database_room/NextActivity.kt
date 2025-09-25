package com.example.local_database_room

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class NextActivity : AppCompatActivity() {
    // UI Components
    private lateinit var backButton: Button
    private lateinit var editContainer: LinearLayout
    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var updateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter

    // Data Components
    private lateinit var dao: StudentDAO
    private var currentStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next)

        // Initialize views
        initializeViews()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup click listeners
        setupClickListeners()

        // Load initial data
        loadStudents()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        editContainer = findViewById(R.id.editContainer)
        editName = findViewById(R.id.editName)
        editAge = findViewById(R.id.editAge)
        updateButton = findViewById(R.id.updateButton)
        cancelButton = findViewById(R.id.cancelButton)
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView)
        dao = AppDatabase.getDatabase(this).studentDao()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            students = emptyList(),
            onEditClick = { student ->
                currentStudent = student
                showEditForm()
            },
            onDeleteClick = { student ->
                showDeleteConfirmationDialog(student)
            }
        )
        studentsRecyclerView.adapter = adapter
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            updateStudent()
        }

        cancelButton.setOnClickListener {
            hideEditForm()
        }
    }

    // UI Helper Methods
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

    // Data Operations
    private fun loadStudents() {
        lifecycleScope.launch {
            val students = dao.getAllStudents()
            adapter.updateStudents(students)
        }
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
                        loadStudents()
                        Toast.makeText(this@NextActivity, "Student updated: ${name}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteStudent(student)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStudent(student: Student) {
        lifecycleScope.launch {
            dao.delete(student)
            loadStudents()
            Toast.makeText(this@NextActivity, "${student.name} deleted", Toast.LENGTH_SHORT).show()
        }
    }
}