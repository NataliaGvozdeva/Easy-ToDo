package com.haraevanton.tasks09.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.ui.adapter.MainAdapter;
import com.haraevanton.tasks09.room.Task;
import com.haraevanton.tasks09.mvp.presenters.MainActivityPresenter;
import com.haraevanton.tasks09.mvp.views.MainActivityView;
import com.haraevanton.tasks09.ui.adapter.RecyclerItemTouchHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    @InjectPresenter
    MainActivityPresenter mainActivityPresenter;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rl)
    RelativeLayout relativeLayout;
    @BindView(R.id.editor_task_name)
    EditText editorTaskName;
    @BindView(R.id.editor_task_status)
    ImageButton editorTaskStatus;
    @BindView(R.id.editor_task_description)
    EditText editorTaskDescription;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
            }

        });

    }

    @Override
    public void onGetDataSuccess(List<Task> tasks) {
        adapter = new MainAdapter(tasks, mainActivityPresenter);
        rv.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);
    }

    @Override
    public void showTaskEditor(Task task) {
        editorTaskStatus.setBackgroundResource(task.getTaskStatus());
        editorTaskName.setText(task.getTaskName());
        editorTaskDescription.setText(task.getTaskDescription());
        relativeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyEditor() {
        editorTaskStatus.setBackgroundResource(R.drawable.ic_task_inactive);
        editorTaskName.setText("");
        editorTaskDescription.setText("");
        relativeLayout.setVisibility(View.VISIBLE);
        mainActivityPresenter.updateCurrentTask(new Task(editorTaskName.getText().toString(), editorTaskDescription.getText().toString(), R.drawable.ic_task_inactive));
    }

    @OnClick(R.id.editor_task_status)
    void onTaskStatusChangeClick(ImageButton view) {
        mainActivityPresenter.onTaskStatusChangeClick(view);
    }

    @OnClick(R.id.editor_cancel)
    public void onEditorCancelClick(ImageButton view) {
        relativeLayout.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.editor_apply)
    public void onEditorApplyClick(ImageButton view) {
        mainActivityPresenter.onEditorApplyClick(view, editorTaskName.getText().toString(), editorTaskDescription.getText().toString());
        relativeLayout.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.fab)
    public void onActionBtnClick(FloatingActionButton fab){
        mainActivityPresenter.onActionBtnClick();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MainAdapter.TaskViewHolder) {
            String name = TaskRepository.get().getTasks().get(viewHolder.getAdapterPosition()).getTaskName();

            final Task deletedItem = TaskRepository.get().getTasks().get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());

//            Snackbar snackbar = Snackbar.make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.snackbar, name), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
