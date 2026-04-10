package com.appfocus

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MonitorService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var usageStatsManager: UsageStatsManager

    private val targetApps = setOf(
        "com.xingin.xhs",
        "com.ss.android.ugc.aweme",
        "com.smile.gifmaker",
        "tv.danmaku.bili"
    )

    private var currentApp: String? = null
    private var startTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        startForeground(1, createNotification())
        startMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "appfocus")
            .setContentTitle("AppFocus")
            .setContentText("正在监控应用使用时间")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                checkCurrentApp()
                delay(1000)
            }
        }
    }

    private fun checkCurrentApp() {
        val endTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(endTime - 1000, endTime)

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val packageName = event.packageName

                if (packageName != currentApp) {
                    if (currentApp != null && targetApps.contains(currentApp)) {
                        val usageTime = endTime - startTime
                        if (usageTime > 3 * 60 * 1000) {
                            lockApp(currentApp!!)
                        }
                    }
                    currentApp = packageName
                    startTime = endTime
                }
            }
        }
    }

    private fun lockApp(packageName: String) {
        AppLockAccessibilityService.performGlobalBack()

        val intent = Intent(this, LockedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("app_name", packageName)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
