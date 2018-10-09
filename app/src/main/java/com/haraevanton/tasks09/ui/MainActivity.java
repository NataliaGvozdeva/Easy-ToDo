package com.haraevanton.tasks09.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.haraevanton.tasks09.NotificationPublisher;
import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.ui.adapter.MainAdapter;
import com.haraevanton.tasks09.room.Task;
import com.haraevanton.tasks09.mvp.presenters.MainActivityPresenter;
import com.haraevanton.tasks09.mvp.views.MainActivityView;
import com.haraevanton.tasks09.ui.adapter.RecyclerItemTouchHelper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.haraevanton.tasks09.App.getContext;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    @InjectPresenter
    MainActivityPresenter mainActivityPresenter;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rl)
    RelativeLayout relativeLayout;
    @BindView(R.id.editor_cancel)
    ImageButton btnCancel;
    @BindView(R.id.editor_apply)
    ImageButton btnApply;
    @BindView(R.id.editor_task_name)
    EditText editorTaskName;
    @BindView(R.id.editor_task_status)
    ImageButton editorTaskStatus;
    @BindView(R.id.editor_task_description)
    EditText editorTaskDescription;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.switch1)
    Switch aSwitch;
    @BindView(R.id.btn_date)
    Button btnDate;
    @BindView(R.id.btn_time)
    Button btnTime;

    private MainAdapter adapter;
    private boolean isSwitched;
    private Calendar notifyCalendar;

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
        aSwitch.setChecked(task.isSwitched());
        btnDate.setEnabled(task.isSwitched());
        btnTime.setEnabled(task.isSwitched());
        notifyCalendar = Calendar.getInstance();
        notifyCalendar.getTimeInMillis();
        if (task.isSwitched()) {
            Log.i("whatswrong", String.valueOf(task.getNotifyDate().get(Calendar.DAY_OF_MONTH)));
            btnDate.setText(task.getNotifyDate().get(Calendar.DAY_OF_MONTH) + "." + (task.getNotifyDate().get(Calendar.MONTH) + 1) + "." + task.getNotifyDate().get(Calendar.YEAR));
            btnTime.setText(task.getNotifyDate().get(Calendar.HOUR_OF_DAY) + ":" + task.getNotifyDate().get(Calendar.MINUTE));
        } else {
            btnDate.setText(R.string.editor_btn_date);
            btnTime.setText(R.string.editor_btn_time);

        }
        relativeLayout.setVisibility(View.VISIBLE);
        btnApply.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);

    }

    @Override
    public void showEmptyEditor() {
        editorTaskStatus.setBackgroundResource(R.drawable.ic_task_active);
        editorTaskName.setText("");
        editorTaskDescription.setText("");
        aSwitch.setChecked(false);
        btnDate.setText(R.string.editor_btn_date);
        btnTime.setText(R.string.editor_btn_time);
        relativeLayout.setVisibility(View.VISIBLE);
        btnApply.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        notifyCalendar = Calendar.getInstance();
        notifyCalendar.getTimeInMillis();
        mainActivityPresenter.updateCurrentTask(new Task(editorTaskName.getText().toString(), editorTaskDescription.getText().toString(), R.drawable.ic_task_active));
    }

    @Override
    public void setNotification(Task task) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Remainder");
        builder.setContentText(task.getTaskName());
        builder.setSmallIcon(R.drawable.ic_task_active);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, task.getId().hashCode());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId().hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getNotifyDate().getTimeInMillis(), pendingIntent);
    }

    @Override
    public void cancelNotification(Task task) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, task.getId().hashCode());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId().hashCode(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    @OnClick(R.id.editor_task_status)
    void onTaskStatusChangeClick(ImageButton view) {
        mainActivityPresenter.onTaskStatusChangeClick(view);
    }

    @OnClick(R.id.editor_cancel)
    public void onEditorCancelClick() {
        relativeLayout.setVisibility(View.GONE);
        btnApply.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.editor_apply)
    public void onEditorApplyClick() {
        mainActivityPresenter.onEditorApplyClick(editorTaskName.getText().toString(), editorTaskDescription.getText().toString(), isSwitched, notifyCalendar);
        relativeLayout.setVisibility(View.GONE);
        btnApply.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

    }

    @OnClick(R.id.btn_date)
    public void onBtnDateClick(Button button) {

        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                notifyCalendar.set(Calendar.YEAR, year);
                notifyCalendar.set(Calendar.MONTH, monthOfYear);
                notifyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Log.i("btnDate", String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear + 1) + "." + String.valueOf(year));
                btnDate.setText(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear + 1) + "." + String.valueOf(year));
//                setInitialDateTime();
            }
        };

        GregorianCalendar gc = new GregorianCalendar();
        new DatePickerDialog(MainActivity.this, d,
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH),
                gc.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick(R.id.btn_time)
    public void onBtnTimeClick(Button button) {

        TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                notifyCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                notifyCalendar.set(Calendar.MINUTE, minute);
                notifyCalendar.set(Calendar.SECOND, 0);
                btnTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                Log.i("dateinfo", String.valueOf(notifyCalendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(notifyCalendar.get(Calendar.MONTH) + 1) + "." + String.valueOf(notifyCalendar.get(Calendar.YEAR)) + " " + String.valueOf(notifyCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(notifyCalendar.get(Calendar.MINUTE)));
//                setInitialDateTime();
            }
        };

        GregorianCalendar gc = new GregorianCalendar();
        new TimePickerDialog(MainActivity.this, t,
                gc.get(Calendar.HOUR_OF_DAY),
                gc.get(Calendar.MINUTE), true)
                .show();
    }

    @OnClick(R.id.fab)
    public void onActionBtnClick() {
        mainActivityPresenter.onActionBtnClick();
    }

    @OnCheckedChanged(R.id.switch1)
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        isSwitched = isChecked;
//        if (isChecked) {
//            btnDate.setEnabled(true);
//            btnTime.setEnabled(true);
//        } else {
//            btnDate.setEnabled(false);
//            btnTime.setEnabled(false);
//        }
        btnDate.setEnabled(isChecked);
        btnTime.setEnabled(isChecked);
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
