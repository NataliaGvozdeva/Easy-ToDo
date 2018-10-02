package com.haraevanton.tasks09.mvp.presenters;

import android.util.Log;
import android.widget.ImageButton;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.room.Task;
import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.mvp.views.MainActivityView;

@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainActivityView> {

    private Task editedTask;
    private int currentTaskStatus;
    private TaskRepository taskRepository;

    public MainActivityPresenter() {


        taskRepository = TaskRepository.get();
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
        if (this.editedTask != null){
            if (!this.editedTask.getId().equals(editedTask.getId())) {
                this.editedTask = editedTask;
            }
        }else {
            this.editedTask = editedTask;
        }

        getViewState().showTaskEditor(editedTask);
    }

    public void updateCurrentTask(Task editedTask) {
        currentTaskStatus = editedTask.getTaskStatus();
        this.editedTask = editedTask;
    }

    public void onEditorApplyClick(String taskName, String taskDescription) {
        if (!taskName.isEmpty()) {
            editedTask.setTaskName(taskName);
            editedTask.setTaskDescription(taskDescription);
            editedTask.setTaskStatus(currentTaskStatus);
            if (!taskRepository.isHaveTask(editedTask.getId())) {
                taskRepository.addTask(editedTask);
            } else {
                taskRepository.updateTask(taskRepository.getTask(editedTask.getId()));
            }
        }
    }

    public void onActionBtnClick() {
        getViewState().showEmptyEditor();
    }

    public void removeTask(String id){
        taskRepository.removeTask(taskRepository.getTask(id));
    }

    public void addTask(int position, Task task){
        taskRepository.refreshTasks(position, task);
    }
}