package com.blackspider.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private Toolbar toolbar;
    private DatabaseHelper databaseHelper;
    private int userId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            // Handle error, user not logged in
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All lists");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab_manage_categories);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        categoryList = databaseHelper.getAllCategories(userId);
        if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(categoryList);
            recyclerView.setAdapter(categoryAdapter);
            categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("CATEGORY_NAME", categoryList.get(position).getName());
                    startActivity(intent);
                }
            });
        } else {
            categoryAdapter.setCategories(categoryList);
            categoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("USER_ID");
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
