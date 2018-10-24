package com.haraevanton.tasks09.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.haraevanton.tasks09.R;
import com.haraevanton.tasks09.ui.MainActivity;

public class WidgetProvider extends AppWidgetProvider {

    public static final String ITEM_POSITION = "item_position";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        PendingIntent mainActivityPI = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT);

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, mainActivityPI);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        int itemPosition = intent.getIntExtra(ITEM_POSITION, -1);
        if (itemPosition != -1){
            Intent mainActivityI = new Intent(context, MainActivity.class);
            mainActivityI.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(mainActivityI);
        }
    }

    void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, PendingIntent mainActivityPI) {
        Intent adapterIntent = new Intent(context, WidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);
        widget.setRemoteAdapter(R.id.widget_list, adapterIntent);
        widget.setOnClickPendingIntent(R.id.widget_title, mainActivityPI);

        Intent listI = new Intent(context, WidgetProvider.class);
        PendingIntent listPI = PendingIntent.getBroadcast(context, 0, listI, 0);
        widget.setPendingIntentTemplate(R.id.widget_list, listPI);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
        appWidgetManager.updateAppWidget(appWidgetId, widget);
    }
}
