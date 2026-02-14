package com.blackspider.todo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private int userId;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCategories();

        FloatingActionButton fab = findViewById(R.id.fab_add_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(databaseHelper.getAllCategories(userId));
        if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(categoryList);
            recyclerView.setAdapter(categoryAdapter);
            categoryAdapter.setOnItemLongClickListener(new CategoryAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(int position) {
                    showEditDeleteDialog(position);
                }
            });
        } else {
            categoryAdapter.notifyDataSetChanged();
        }
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Category");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        final EditText categoryNameInput = view.findViewById(R.id.category_name_input);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = categoryNameInput.getText().toString();
                if (!categoryName.isEmpty()) {
                    databaseHelper.addCategory(categoryName, 0, userId);
                    loadCategories();
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
                        showUpdateCategoryDialog(position);
                        break;
                    case 1:
                        databaseHelper.deleteCategory(categoryList.get(position).getId());
                        loadCategories();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void showUpdateCategoryDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Category");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_category, null);
        final EditText categoryNameInput = view.findViewById(R.id.category_name_input);

        categoryNameInput.setText(categoryList.get(position).getName());

        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = categoryNameInput.getText().toString();
                if (!categoryName.isEmpty()) {
                    databaseHelper.updateCategory(categoryList.get(position).getId(), categoryName, 0);
                    loadCategories();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
}
