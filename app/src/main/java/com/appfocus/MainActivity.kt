package com.appfocus

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appfocus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            if (checkPermission()) {
                startService(Intent(this, MonitorService::class.java))
                Toast.makeText(this, "服务已启动", Toast.LENGTH_SHORT).show()
            } else {
                openPermissionSettings()
            }
        }

        binding.stopButton.setOnClickListener {
            stopService(Intent(this, MonitorService::class.java))
            Toast.makeText(this, "服务已停止", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(): Boolean {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryAndAggregateUsageStats(currentTime - 1000, currentTime)
        return stats.isNotEmpty()
    }

    private fun openPermissionSettings() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
}
