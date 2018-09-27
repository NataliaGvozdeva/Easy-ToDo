package com.haraevanton.tasks09.mvp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskRepository {
    private static TaskRepository taskRepository;

    private List<Task> tasks;

    public static TaskRepository get() {
        if (taskRepository == null){
            taskRepository = new TaskRepository();
        }

        return taskRepository;
    }

    private TaskRepository() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTask(UUID id) {
        for (Task task : tasks) {
            if (task.getTaskId().equals(id)) {
                return task;
            }
        }
        return null;
    }
}
