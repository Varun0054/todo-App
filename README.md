# To-Do List Android App

A simple and intuitive to-do list application for Android. It helps users organize their tasks by creating custom categories, setting due dates, and tracking their progress through a clean and user-friendly interface.

## Features

*   **User Authentication**: Secure user registration and login system with password hashing (using bcrypt).
*   **Persistent Sessions**: Users remain logged in until they explicitly log out.
*   **Category Management**:
    *   Create custom categories to group tasks.
    *   View all categories on the main dashboard.
    *   Edit and delete existing categories.
*   **Task Management**:
    *   Add tasks with a title and due date to a specific category.
    *   View all tasks listed under their respective categories.
    *   Update and delete tasks.
*   **Intuitive UI**: A clean and simple interface built with Material Design components.

## Screenshots

*(You can add screenshots of your app here to showcase its features)*

| Login Screen | Main Dashboard | Task List |
| :---: | :---: | :---: |
| ![WhatsApp Image 2026-02-14 at 4 19 19 PM](https://github.com/user-attachments/assets/582e2c72-da1f-48ac-8c97-8fa14fa0ef45)
 | ![WhatsApp Image 2026-02-14 at 4 19 18 PM (1)](https://github.com/user-attachments/assets/969bdad9-57c2-4ebd-b126-0078980a371d)
 | ![WhatsApp Image 2026-02-14 at 4 19 18 PM](https://github.com/user-attachments/assets/e67b91c3-af73-4d8e-8302-1f01c8b74d18)
 |

## Technologies Used

*   **Language**: Java
*   **Database**: SQLite for local data storage.
*   **UI**:
    *   Android SDK with AppCompat
    *   Material Components for Android (Buttons, Text Fields, FloatingActionButton)
    *   RecyclerView for displaying lists of categories and tasks.
*   **Security**: `at.favre.lib:bcrypt` for hashing user passwords.

## Setup and Installation

1.  **Clone the repository**:
    ```bash
    git clone <your-repository-link>
    ```
2.  **Open in Android Studio**:
    *   Open Android Studio.
    *   Click on `File > Open`.
    *   Navigate to the cloned project directory and select it.
3.  **Sync Dependencies**:
    *   Android Studio will automatically sync the project's Gradle dependencies as listed in the `build.gradle` file.
4.  **Run the App**:
    *   Connect an Android device or start an emulator.
    *   Click the `Run` button (▶) in Android Studio.

