package com.victorfaria.juntos.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.victorfaria.juntos.DateStore
import com.victorfaria.juntos.TimeUtils

/**
 * Agenda um alarme (não-exato, não precisa de permissão especial) para o próximo instante em que
 * a contagem de dias vira, para atualizar o widget nesse momento e reiniciar a base do Chronometer.
 * O alarme se reagenda sozinho a cada disparo.
 */
object AlarmScheduler {

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, RomanticWidgetProvider::class.java).apply {
            action = RomanticWidgetProvider.ACTION_DAILY_TICK
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    fun scheduleNextTick(context: Context) {
        val target = DateStore.getTargetMillis(context) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = TimeUtils.nextAnniversaryMillis(target)
        val pi = pendingIntent(context)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }
}
