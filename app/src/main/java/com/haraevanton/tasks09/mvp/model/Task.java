package com.haraevanton.tasks09.mvp.model;

import com.haraevanton.tasks09.R;

import java.util.UUID;

public class Task {

    private UUID taskId;
    private String taskName;
    private String taskDescription;
    private int taskStatus;

    public Task(String taskName, String taskDescription){
        taskId = UUID.randomUUID();
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        taskStatus = R.drawable.ic_task_active;
    }

    public Task(String taskName, String taskDescription, int taskStatus){
        taskId = UUID.randomUUID();
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
}
