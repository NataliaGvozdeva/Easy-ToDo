package com.haraevanton.tasks09.mvp.presenters;

import android.widget.ImageButton;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.haraevanton.tasks09.App;
import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.room.Task;
import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.mvp.views.MainActivityView;

import java.util.Calendar;

import javax.inject.Inject;

@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainActivityView> {

    @Inject
    TaskRepository taskRepository;

    private Task editedTask;
    private int currentTaskStatus;

    public MainActivityPresenter() {

        App.getComponent().injectPresenter(this);

        getViewState().onGetDataSuccess(taskRepository.getTasks());

    }

    public void onTaskStatusChangeClick(ImageButton view) {
        if (currentTaskStatus == R.drawable.ic_task_active) {
            view.setBackgroundResource(R.drawable.ic_task_inactive);
            currentTaskStatus = R.drawable.ic_task_inactive;
        } else {
            view.setBackgroundResource(R.drawable.ic_task_active);
            currentTaskStatus = R.drawable.ic_task_active;
        }
    }

    public void onTaskNameClick(Task editedTask) {
        currentTaskStatus = editedTask.getTaskStatus();
        if (this.editedTask != null) {
            if (!this.editedTask.getId().equals(editedTask.getId())) {
                this.editedTask = editedTask;
            }
        } else {
            this.editedTask = editedTask;
        }

        getViewState().showTaskEditor(editedTask);
    }

    public void updateCurrentTask(Task editedTask) {
        currentTaskStatus = editedTask.getTaskStatus();
        this.editedTask = editedTask;
    }

    public void onEditorApplyClick(String taskName, String taskDescription, boolean isSwitched, Calendar notifyCalendar) {
        if (!taskName.isEmpty()) {
            editedTask.setTaskName(taskName);
            editedTask.setTaskDescription(taskDescription);
            editedTask.setTaskStatus(currentTaskStatus);
            editedTask.setSwitched(isSwitched);
            editedTask.setNotifyDate(notifyCalendar);
            if (isSwitched && (Calendar.getInstance().compareTo(editedTask.getNotifyDate()) < 0)) {
                getViewState().setNotification(editedTask);
            } else {
                getViewState().cancelNotification(editedTask);
            }
            if (!taskRepository.isHaveTask(editedTask.getId())) {
                taskRepository.addTask(editedTask);
                getViewState().updateWidget();
            } else {
                taskRepository.updateTask(taskRepository.getTask(editedTask.getId()));
                getViewState().updateWidget();
            }
        }
    }

    public void onActionBtnClick() {
        getViewState().showEmptyEditor();
    }

    public void removeTask(String id) {
        getViewState().cancelNotification(taskRepository.getTask(id));
        taskRepository.removeTask(taskRepository.getTask(id));
        getViewState().updateWidget();
    }

    public void addTask(int position, Task task) {
        if (task.isSwitched() && (Calendar.getInstance().compareTo(task.getNotifyDate()) < 0)) {
            getViewState().setNotification(task);
        }
        taskRepository.refreshTasks(position, task);
        getViewState().updateWidget();
    }

}