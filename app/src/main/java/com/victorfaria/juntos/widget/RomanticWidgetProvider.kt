package com.victorfaria.juntos.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import com.victorfaria.juntos.DateStore
import com.victorfaria.juntos.MainActivity
import com.victorfaria.juntos.R
import com.victorfaria.juntos.TimeUtils

class RomanticWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_DAILY_TICK = "com.victorfaria.juntos.ACTION_DAILY_TICK"
        const val ACTION_TOGGLE_MODE = "com.victorfaria.juntos.ACTION_TOGGLE_MODE"

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, RomanticWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                updateWidgets(context, manager, ids)
            }
        }

        fun updateWidgets(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
            val target = DateStore.getTargetMillis(context)
            for (id in appWidgetIds) {
                val views = buildRemoteViews(context, target)
                manager.updateAppWidget(id, views)
            }
        }

        private fun buildRemoteViews(context: Context, targetMillis: Long?): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_romantic)

            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPendingIntent = PendingIntent.getActivity(
                context, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_content, openAppPendingIntent)

            val toggleIntent = Intent(context, RomanticWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_MODE
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context, 1, toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_toggle, togglePendingIntent)

            if (targetMillis == null) {
                views.setTextViewText(R.id.widget_days, context.getString(R.string.widget_no_date))
                views.setViewVisibility(R.id.widget_dot, android.view.View.GONE)
                views.setViewVisibility(R.id.widget_chronometer, android.view.View.GONE)
                views.setChronometer(R.id.widget_chronometer, SystemClock.elapsedRealtime(), null, false)
                return views
            }

            val elapsed = TimeUtils.elapsedSince(targetMillis)
            val label = when (DateStore.getDisplayMode(context)) {
                DateStore.MODE_FULL -> TimeUtils.formatBreakdownLabel(TimeUtils.calendarBreakdown(targetMillis))
                else -> TimeUtils.formatDaysLabel(elapsed.days)
            }
            views.setTextViewText(R.id.widget_days, label)
            views.setViewVisibility(R.id.widget_dot, android.view.View.VISIBLE)
            views.setViewVisibility(R.id.widget_chronometer, android.view.View.VISIBLE)

            val base = SystemClock.elapsedRealtime() - elapsed.remainderMillis
            views.setChronometer(R.id.widget_chronometer, base, null, true)

            return views
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
        AlarmScheduler.scheduleNextTick(context)
    }

    override fun onEnabled(context: Context) {
        AlarmScheduler.scheduleNextTick(context)
    }

    override fun onDisabled(context: Context) {
        AlarmScheduler.cancel(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DAILY_TICK, Intent.ACTION_BOOT_COMPLETED -> {
                updateAllWidgets(context)
                AlarmScheduler.scheduleNextTick(context)
            }
            ACTION_TOGGLE_MODE -> {
                DateStore.toggleDisplayMode(context)
                updateAllWidgets(context)
            }
            else -> super.onReceive(context, intent)
        }
    }
}
