package com.blackspider.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 3; // Incremented version

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Tasks table
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_USER_ID = "user_id";
    private static final String COLUMN_TASK_CATEGORY = "category";
    private static final String COLUMN_TASK_DUE_DATE = "due_date";

    // Categories table
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_COLOR = "color";
    private static final String COLUMN_CATEGORY_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TASK_TITLE + " TEXT,"
                + COLUMN_TASK_DUE_DATE + " TEXT,"
                + COLUMN_TASK_CATEGORY + " TEXT,"
                + COLUMN_TASK_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_CATEGORY_COLOR + " INTEGER,"
                + COLUMN_CATEGORY_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CATEGORY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            values.put(COLUMN_PASSWORD, hashedPassword);

            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (Exception e) {
            return false;
        }
    }

    public int checkUser(String username, String password) {
        int userId = -1;
        if (password == null) return -1;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String[] columns = {COLUMN_USER_ID, COLUMN_PASSWORD};
            String selection = COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {username};

            try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
                if (cursor.moveToFirst()) {
                    String hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
                    if (result.verified) {
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                    }
                }
            }
        } catch (Exception e) {
            // Log exception
        }
        return userId;
    }

    public boolean addTask(String title, String dueDate, String category, int userId) {
        if (title == null || dueDate == null || category == null) {
            return false;
        }
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TASK_TITLE, title);
            values.put(COLUMN_TASK_DUE_DATE, dueDate);
            values.put(COLUMN_TASK_CATEGORY, category);
            values.put(COLUMN_TASK_USER_ID, userId);
            return db.insert(TABLE_TASKS, null, values) != -1;
        }
    }

    public boolean updateTask(int id, String title, String dueDate) {
        if (title == null || dueDate == null) {
            return false;
        }
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TASK_TITLE, title);
            values.put(COLUMN_TASK_DUE_DATE, dueDate);
            return db.update(TABLE_TASKS, values, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        }
    }

    public boolean deleteTask(int id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            return db.delete(TABLE_TASKS, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        }
    }

    public List<Task> getTasksForCategory(int userId, String category) {
        List<Task> tasks = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selection = COLUMN_TASK_USER_ID + " = ? AND " + COLUMN_TASK_CATEGORY + " = ?";
            String[] selectionArgs = {String.valueOf(userId), category};
            try (Cursor cursor = db.query(TABLE_TASKS, null, selection, selectionArgs, null, null, null)) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE));
                    String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DUE_DATE));
                    tasks.add(new Task(id, title, dueDate, category));
                }
            }
        }
        return tasks;
    }

    public List<Category> getAllCategories(int userId) {
        List<Category> categories = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selection = COLUMN_CATEGORY_USER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(userId)};
            try (Cursor cursor = db.query(TABLE_CATEGORIES, null, selection, selectionArgs, null, null, null)) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
                    int color = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_COLOR));
                    categories.add(new Category(id, name, color));
                }
            }
        }
        return categories;
    }

    public boolean addCategory(String name, int color, int userId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, name);
            values.put(COLUMN_CATEGORY_COLOR, color);
            values.put(COLUMN_CATEGORY_USER_ID, userId);
            return db.insert(TABLE_CATEGORIES, null, values) != -1;
        }
    }

    public boolean updateCategory(int id, String name, int color) {
        if (name == null) {
            return false;
        }
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, name);
            values.put(COLUMN_CATEGORY_COLOR, color);
            return db.update(TABLE_CATEGORIES, values, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        }
    }

    public boolean deleteCategory(int id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            return db.delete(TABLE_CATEGORIES, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        }
    }

    public long getTasksCount(int userId) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            return db.compileStatement("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_USER_ID + " = " + userId).simpleQueryForLong();
        }
    }

    public long getTodayTasksCount(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            return db.compileStatement("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_USER_ID + " = " + userId + " AND " + COLUMN_TASK_DUE_DATE + " = '" + today + "'").simpleQueryForLong();
        }
    }

    public long getThisWeekTasksCount(int userId) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
             return db.compileStatement("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_USER_ID + " = " + userId + " AND " + "date(" + COLUMN_TASK_DUE_DATE + ") >= date('now', 'weekday 0', '-6 days') AND date(" + COLUMN_TASK_DUE_DATE + ") <= date('now', 'weekday 0')").simpleQueryForLong();
        }
    }
    
    public long getThisMonthTasksCount(int userId) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            return db.compileStatement("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_USER_ID + " = " + userId + " AND " + "strftime('%Y-%m', " + COLUMN_TASK_DUE_DATE + ") = strftime('%Y-%m', 'now')").simpleQueryForLong();
        }
    }
    
    public long getCategorizedTasksCount(int userId, String category) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            return db.compileStatement("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_USER_ID + " = " + userId + " AND " + COLUMN_TASK_CATEGORY + " = '" + category + "'").simpleQueryForLong();
        }
    }
}
