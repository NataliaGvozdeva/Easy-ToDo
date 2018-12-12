package com.haraevanton.easytodo.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.haraevanton.easytodo.room.Task;

import java.util.List;

public interface MainActivityView extends MvpView {

    void onGetDataSuccess(List<Task> tasks);

    void showTaskEditor(Task task);

    void showEmptyEditor();

    void setNotification(Task task);

    void cancelNotification(Task task);

    void updateWidget();
}
