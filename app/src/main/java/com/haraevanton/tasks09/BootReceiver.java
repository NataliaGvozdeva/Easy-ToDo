package com.haraevanton.tasks09;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.haraevanton.tasks09.mvp.model.TaskRepository;
import com.haraevanton.tasks09.room.Task;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ReceiverBOOT", "hello");

        List<Task> tasks = TaskRepository.get().getTasks();
        for(Task task : tasks){
            if ((task.isSwitched()) && (Calendar.getInstance().compareTo(task.getNotifyDate()) < 0)){
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle("Remainder");
                builder.setContentText(task.getTaskName());
                builder.setSmallIcon(R.drawable.ic_task_active);
                Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, task.getId().hashCode());
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId().hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, task.getNotifyDate().getTimeInMillis(), pendingIntent);
            }
        }

    }
}
