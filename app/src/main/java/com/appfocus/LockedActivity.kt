package com.appfocus

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.appfocus.databinding.ActivityLockedBinding

class LockedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appName = intent.getStringExtra("app_name") ?: "应用"
        binding.messageText.text = "$appName 使用时间已达上限"

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = "${millisUntilFinished / 1000} 秒"
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }

    override fun onBackPressed() {
        // 禁用返回键
    }
}
