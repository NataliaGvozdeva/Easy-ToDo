package com.haraevanton.tasks09.mvp.presenters;

import android.widget.ImageButton;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.mvp.model.Task;
import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.mvp.views.MainActivityView;

import java.util.List;

@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainActivityView> {

    private List<Task> tasks;
    private Task editedTask;
    private int currentTaskStatus;
    private TaskRepository taskRepository;

    public MainActivityPresenter() {

        taskRepository = TaskRepository.get();

        if (taskRepository.getTasks().isEmpty()) {

            taskRepository = TaskRepository.get();
            taskRepository.addTask(new Task("First task", "description example, here will be some task"));
            taskRepository.addTask(new Task("Second task", "description example, here will be some task"));
            taskRepository.addTask(new Task("Third task", "description example, here will be some task", R.drawable.ic_task_inactive));

            tasks = taskRepository.getTasks();

            getViewState().onGetDataSuccess(tasks);
        } else {

            tasks = taskRepository.getTasks();
            getViewState().onGetDataSuccess(tasks);
        }

    }

    public void onTaskStatusChangeClick(ImageButton view){
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
        this.editedTask = editedTask;
        getViewState().showTaskEditor(editedTask);
    }

    public void onEditorApplyClick(ImageButton view, String taskName, String taskDescription){
        editedTask.setTaskName(taskName);
        editedTask.setTaskDescription(taskDescription);
        editedTask.setTaskStatus(currentTaskStatus);
    }

    public void onActionBtnClick(){
        Task newTask = new Task("New task", "New task description");
        this.editedTask = newTask;
        currentTaskStatus = editedTask.getTaskStatus();
        taskRepository.addTask(newTask);
        getViewState().showTaskEditor(newTask);
    }
}
