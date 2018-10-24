package com.haraevanton.tasks09.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.room.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<Task> tasks;
    private Context context;

    public ViewFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        tasks = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        tasks = TaskRepository.get().getTasks();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_item_title, tasks.get(i).getTaskName());
        if (tasks.get(i).isSwitched()){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ENGLISH);
            String str = sdf.format(tasks.get(i).getNotifyDate().getTime());
            remoteViews.setTextViewText(R.id.widget_item_notify_date, str);
        } else {
            remoteViews.setTextViewText(R.id.widget_item_notify_date, "");
        }
        Intent clickIntent = new Intent();
        clickIntent.putExtra(WidgetProvider.ITEM_POSITION, i);
        remoteViews.setOnClickFillInIntent(R.id.widget_item_title, clickIntent);
        Log.i("widgetSetTitle", tasks.get(0).getTaskName());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
