package com.haraevanton.easytodo.mvp.model;

import com.haraevanton.easytodo.App;
import com.haraevanton.easytodo.room.Task;
import com.haraevanton.easytodo.room.TaskDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskRepository {

    private static TaskRepository taskRepository;

    @Inject
    TaskDao taskDao;

    private CompositeDisposable compositeDisposable;

    private List<Task> tasks;

    public static TaskRepository get() {
        if (taskRepository == null) {
            taskRepository = new TaskRepository();
        }

        return taskRepository;
    }

    private TaskRepository() {

        App.getComponent().injectRepository(this);

        compositeDisposable = new CompositeDisposable();

        if (tasks == null) {
            tasks = new ArrayList<>();
        } else {
            tasks.clear();
        }

        Disposable taskDisposable = taskDao.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskFetched);
        compositeDisposable.add(taskDisposable);
    }

    private void onTaskFetched(List<Task> tasks) {
        this.tasks.addAll(tasks);
        Collections.reverse(this.tasks);

    }

    public void addTask(Task t) {
        taskDao.insertAll(t);
        tasks.add(0, t);
    }

    public void addTask(Task t, int position) {
        tasks.add(position, t);
    }

    public void updateTask(Task t) {
        taskDao.update(t);
    }

    public void removeTask(Task t) {
        taskDao.delete(t);
        tasks.remove(t);
    }

    public void refreshTasks() {
        taskDao.deleteAll();
        Collections.reverse(tasks);
        for (Task t : tasks) {
            taskDao.insertAll(t);
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

    public void onCleared() {
        compositeDisposable.clear();
    }

}