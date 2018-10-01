package com.haraevanton.tasks09.mvp.model;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.haraevanton.tasks09.App;
import com.haraevanton.tasks09.room.AppDatabase;
import com.haraevanton.tasks09.room.Task;

import java.util.Collections;
import java.util.List;

public class TaskRepository {

    private static TaskRepository taskRepository;

    private List<Task> tasks;
    private Context context;
    private AppDatabase db;

    public static TaskRepository get() {
        if (taskRepository == null){
            taskRepository = new TaskRepository();
        }

        return taskRepository;
    }

    private TaskRepository() {
        context = App.getContext();
        db = Room.databaseBuilder(App.getContext(), AppDatabase.class, "taskdb")
                .allowMainThreadQueries()
                .build();

        if (tasks != null){
            tasks.clear();
        }

        tasks = db.taskDao().getAllTasks();
        Collections.reverse(tasks);

    }

    public void addTask(Task t) {
        db.taskDao().insertAll(t);
        tasks.add(0, t);
    }

    public void addTask(int position, Task t) {
        db.taskDao().insertAll(t);
        tasks.add(position, t);
    }

    public void updateTask(Task t) {
        db.taskDao().update(t);
    }

    public void removeTask(Task t) {
        db.taskDao().delete(t);
        tasks.remove(t);
    }

    public void refreshTasks(int position, Task task){
        tasks.add(position, task);
        db.taskDao().deleteAll();
        Collections.reverse(tasks);
        for (Task t : tasks){
            db.taskDao().insertAll(t);
        }
        Collections.reverse(tasks);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTask(String id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    public boolean isHaveTask(String id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}