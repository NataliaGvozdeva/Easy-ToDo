package com.haraevanton.tasks09.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.haraevanton.tasks09.mvp.model.Task;

import java.util.List;

public interface MainActivityView extends MvpView {

    void onGetDataSuccess(List<Task> tasks);

    void showTaskEditor(Task task);
}