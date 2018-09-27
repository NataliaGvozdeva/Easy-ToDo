package com.haraevanton.tasks09.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.mvp.model.Task;
import com.haraevanton.tasks09.mvp.presenters.MainActivityPresenter;
import com.haraevanton.tasks09.ui.MainActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.TaskViewHolder> {

    private MainActivityPresenter mainActivityPresenter;
    private List<Task> tasks;

    public MainAdapter(List<Task> tasks, MainActivityPresenter mainActivityPresenter) {
        this.tasks = tasks;
        this.mainActivityPresenter = mainActivityPresenter;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ButterKnife.bind(this, parent.getRootView());
        return new TaskViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false), mainActivityPresenter);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.taskName.setText(tasks.get(position).getTaskName());
        holder.taskStatus.setBackgroundResource(tasks.get(position).getTaskStatus());
        holder.description.setText(tasks.get(position).getTaskDescription());
        holder.bind(tasks);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.task_name)
        TextView taskName;
        @BindView(R.id.task_status)
        ImageButton taskStatus;
        @BindView(R.id.description)
        TextView description;

        private MainActivityPresenter mainActivityPresenter;
        private List<Task> tasks;

        public TaskViewHolder(@NonNull View itemView, MainActivityPresenter mainActivityPresenter) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mainActivityPresenter = mainActivityPresenter;
        }

        public void bind(List<Task> tasks){
            this.tasks = tasks;
        }

        @OnClick(R.id.expand)
        void onExpandClick(ImageButton view){
            if (description.getVisibility() == View.GONE){
                description.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.ic_expand_less);
            } else {
                description.setVisibility(View.GONE);
                view.setBackgroundResource(R.drawable.ic_expand_more);
            }
        }

        @OnClick(R.id.task_name)
        void onTaskNameCLick(TextView taskName){
            for (Task task: tasks) {
                if (taskName.getText() == task.getTaskName()){
                    mainActivityPresenter.onTaskNameClick(task);
                    Log.i("task1", "onTaskNameCLick");
                }
            }
        }

    }
}
