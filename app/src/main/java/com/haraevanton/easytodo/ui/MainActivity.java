package com.haraevanton.easytodo.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.haraevanton.easytodo.NotificationPublisher;
import com.haraevanton.easytodo.R;
import com.haraevanton.easytodo.mvp.model.TaskRepository;
import com.haraevanton.easytodo.ui.adapter.MainAdapter;
import com.haraevanton.easytodo.room.Task;
import com.haraevanton.easytodo.mvp.presenters.MainActivityPresenter;
import com.haraevanton.easytodo.mvp.views.MainActivityView;
import com.haraevanton.easytodo.ui.adapter.RecyclerItemTouchHelper;
import com.haraevanton.easytodo.widget.WidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String FIRST_START = "first_start";

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

    public boolean isFirstStart;


    private MainAdapter adapter;
    private boolean isSwitched;
    private Calendar notifyCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SharedPreferences getSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        isFirstStart = getSharedPreferences.getBoolean(FIRST_START, true);

        if (isFirstStart) {

            Intent i = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(i);
            SharedPreferences.Editor e = getSharedPreferences.edit();
            e.putBoolean(FIRST_START, false);
            e.apply();
        }

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
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);
    }

    @Override
    public void showTaskEditor(Task task) {
        editorTaskStatus.setBackgroundResource(task.getTaskStatus());
        editorTaskName.setText(task.getTaskName());
        editorTaskDescription.setText(task.getTaskDescription());
        aSwitch.setChecked(task.isSwitched());
        isSwitched = task.isSwitched();
        btnDate.setEnabled(task.isSwitched());
        if (Calendar.getInstance().compareTo(task.getNotifyDate()) < 0) {
            notifyCalendar = Calendar.getInstance();
            notifyCalendar.getTimeInMillis();
        } else {
            notifyCalendar = task.getNotifyDate();
        }
        if (task.isSwitched()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ENGLISH);
            String str = sdf.format(task.getNotifyDate().getTime());
            btnDate.setText(str);
        } else {
            btnDate.setText(R.string.editor_btn_date);

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
        isSwitched = false;
        btnDate.setText(R.string.editor_btn_date);
        relativeLayout.setVisibility(View.VISIBLE);
        btnApply.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        notifyCalendar = Calendar.getInstance();
        notifyCalendar.getTimeInMillis();
        mainActivityPresenter.updateCurrentTask(new Task(editorTaskName.getText().toString(),
                editorTaskDescription.getText().toString(),
                R.drawable.ic_task_active));
    }

    @Override
    public void setNotification(Task task) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.notification));
        builder.setContentText(task.getTaskName());
        builder.setSmallIcon(task.getTaskStatus());
        builder.setContentIntent(PendingIntent.getActivity(this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT));
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, task.getId().hashCode());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                task.getId().hashCode(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    task.getNotifyDate().getTimeInMillis(),
                    pendingIntent);
        }
    }

    @Override
    public void cancelNotification(Task task) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, task.getId().hashCode());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                task.getId().hashCode(),
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

    }

    @Override
    public void updateWidget() {
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this)
                .getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
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
        hideKeyboard(this);
    }

    @OnClick(R.id.editor_apply)
    public void onEditorApplyClick() {
        mainActivityPresenter.onEditorApplyClick(editorTaskName.getText().toString(),
                editorTaskDescription.getText().toString(),
                isSwitched,
                notifyCalendar);
        relativeLayout.setVisibility(View.GONE);
        btnApply.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        hideKeyboard(this);

    }

    @OnClick(R.id.btn_date)
    public void onBtnDateClick() {

        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                notifyCalendar.set(Calendar.YEAR, year);
                notifyCalendar.set(Calendar.MONTH, monthOfYear);
                notifyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                onBtnTimeClick();
            }
        };

        GregorianCalendar gc = new GregorianCalendar();
        new DatePickerDialog(MainActivity.this, d,
                gc.get(Calendar.YEAR),
                gc.get(Calendar.MONTH),
                gc.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void onBtnTimeClick() {

        TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                notifyCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                notifyCalendar.set(Calendar.MINUTE, minute);
                notifyCalendar.set(Calendar.SECOND, 0);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy",
                        Locale.ENGLISH);
                String str = sdf.format(notifyCalendar.getTime());
                btnDate.setText(str);
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
        btnDate.setEnabled(isChecked);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MainAdapter.TaskViewHolder) {
            String name = TaskRepository.get().getTasks()
                    .get(viewHolder.getAdapterPosition()).getTaskName();
            final Task deletedItem = TaskRepository.get().getTasks()
                    .get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());

            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                    getString(R.string.snackbar, name),
                    Snackbar.LENGTH_LONG);
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

    @Override
    public void onMoved(int fromPos, int toPos) {
        adapter.moveItem(fromPos, toPos);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
