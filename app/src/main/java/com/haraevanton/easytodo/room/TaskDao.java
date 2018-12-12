package com.haraevanton.easytodo.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface TaskDao {

    @Insert
    void insertAll(Task... tasks);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task")
    Single<List<Task>> getAllTasks();

    @Query("DELETE FROM task")
    void deleteAll();

}