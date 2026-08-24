package com.victorfaria.juntos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.victorfaria.juntos.databinding.ActivityMainBinding
import com.victorfaria.juntos.widget.AlarmScheduler
import com.victorfaria.juntos.widget.RomanticWidgetProvider
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var pickedCalendar: Calendar = Calendar.getInstance()

    private val tickHandler = Handler(Looper.getMainLooper())
    private val tickRunnable = object : Runnable {
        override fun run() {
            refreshPreview()
            tickHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DateStore.getTargetMillis(this)?.let {
            pickedCalendar.timeInMillis = it
        }
        updateSelectedDateLabel()

        binding.btnPickDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveDate() }
        binding.btnAddWidget.setOnClickListener { requestPinWidget() }
    }

    override fun onResume() {
        super.onResume()
        tickHandler.post(tickRunnable)
    }

    override fun onPause() {
        super.onPause()
        tickHandler.removeCallbacks(tickRunnable)
    }

    private fun showDatePicker() {
        val year = pickedCalendar.get(Calendar.YEAR)
        val month = pickedCalendar.get(Calendar.MONTH)
        val day = pickedCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            pickedCalendar.set(Calendar.YEAR, y)
            pickedCalendar.set(Calendar.MONTH, m)
            pickedCalendar.set(Calendar.DAY_OF_MONTH, d)
            showTimePicker()
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val hour = pickedCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = pickedCalendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, h, min ->
            pickedCalendar.set(Calendar.HOUR_OF_DAY, h)
            pickedCalendar.set(Calendar.MINUTE, min)
            pickedCalendar.set(Calendar.SECOND, 0)
            pickedCalendar.set(Calendar.MILLISECOND, 0)
            updateSelectedDateLabel()
            refreshPreview()
        }, hour, minute, true).show()
    }

    private fun updateSelectedDateLabel() {
        binding.tvSelectedDate.text = getString(
            R.string.selected_date_format,
            TimeUtils.formatDate(pickedCalendar.timeInMillis)
        )
    }

    private fun saveDate() {
        DateStore.setTargetMillis(this, pickedCalendar.timeInMillis)
        AlarmScheduler.scheduleNextTick(this)
        RomanticWidgetProvider.updateAllWidgets(this)
        refreshPreview()
        Toast.makeText(this, R.string.date_saved, Toast.LENGTH_SHORT).show()
    }

    private fun refreshPreview() {
        val elapsed = TimeUtils.elapsedSince(pickedCalendar.timeInMillis)
        binding.tvPreviewDays.text = TimeUtils.formatDaysLabel(elapsed.days)
        binding.tvPreviewClock.text = String.format(
            "%02d:%02d:%02d", elapsed.hours, elapsed.minutes, elapsed.seconds
        )
    }

    private fun requestPinWidget() {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val provider = ComponentName(this, RomanticWidgetProvider::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(provider, null, null)
        } else {
            Toast.makeText(this, R.string.add_widget_manually, Toast.LENGTH_LONG).show()
        }
    }
}
