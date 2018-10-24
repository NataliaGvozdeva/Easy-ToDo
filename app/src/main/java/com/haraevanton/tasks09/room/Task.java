package com.haraevanton.tasks09.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Task {

    @PrimaryKey
    @NonNull
    private String id;
    private String taskName;
    private String taskDescription;
    private int taskStatus;
    private boolean isSwitched;
    private Calendar notifyDate;

    public Task(String taskName, String taskDescription, int taskStatus){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isSwitched() {
        return isSwitched;
    }

    public void setSwitched(boolean switched) {
        isSwitched = switched;
    }

    public Calendar getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Calendar notifyDate) {
        this.notifyDate = notifyDate;
    }
}
