package com.blackspider.todo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper databaseHelper;
    private int userId;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        userId = getIntent().getIntExtra("USER_ID", -1);
        category = getIntent().getStringExtra("CATEGORY_NAME");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        loadTasks();

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });

        taskAdapter.setOnItemLongClickListener(new TaskAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showEditDeleteDialog(position);
            }
        });
    }

    private void loadTasks() {
        taskList = databaseHelper.getTasksForCategory(userId, category);
        taskAdapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(taskAdapter);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        final EditText taskNameInput = view.findViewById(R.id.task_name_input);
        final EditText dueDateInput = view.findViewById(R.id.due_date_input);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskName = taskNameInput.getText().toString();
                String dueDate = dueDateInput.getText().toString();
                if (!taskName.isEmpty()) {
                    databaseHelper.addTask(taskName, dueDate, category, userId);
                    loadTasks();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showEditDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action");
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showUpdateTaskDialog(position);
                        break;
                    case 1:
                        databaseHelper.deleteTask(taskList.get(position).getId());
                        loadTasks();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void showUpdateTaskDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Task");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_task, null);
        final EditText taskNameInput = view.findViewById(R.id.task_name_input);
        final EditText dueDateInput = view.findViewById(R.id.due_date_input);

        taskNameInput.setText(taskList.get(position).getTitle());
        dueDateInput.setText(taskList.get(position).getDueDate());

        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskName = taskNameInput.getText().toString();
                String dueDate = dueDateInput.getText().toString();
                if (!taskName.isEmpty()) {
                    databaseHelper.updateTask(taskList.get(position).getId(), taskName, dueDate);
                    loadTasks();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
