package com.haraevanton.easytodo.mvp.presenters;

import android.widget.ImageButton;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.haraevanton.easytodo.App;
import com.haraevanton.easytodo.R;
import com.haraevanton.easytodo.room.Task;
import com.haraevanton.easytodo.mvp.model.TaskRepository;
import com.haraevanton.easytodo.mvp.views.MainActivityView;

import java.util.Calendar;
import java.util.Collections;

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

    public void onEditorApplyClick(String taskName, String taskDescription,
                                   boolean isSwitched, Calendar notifyCalendar) {
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

    public void restoreTask(int position, Task task) {
        if (task.isSwitched() && (Calendar.getInstance().compareTo(task.getNotifyDate()) < 0)) {
            getViewState().setNotification(task);
        }
        taskRepository.addTask(task, position);
        taskRepository.refreshTasks();
        getViewState().updateWidget();
    }

    public void moveTask(int fromPos, int toPos) {
        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(TaskRepository.get().getTasks(), i, i + 1);
            }
        } else {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(TaskRepository.get().getTasks(), i, i - 1);
            }
        }

        taskRepository.refreshTasks();
        getViewState().updateWidget();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskRepository.onCleared();
    }
}