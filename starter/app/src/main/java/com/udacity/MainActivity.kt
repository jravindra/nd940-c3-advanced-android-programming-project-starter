package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
        raddioGroup.setOnCheckedChangeListener { _, _ -> custom_button.reset() }

    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notificationManager.cancelNotifications()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            var downloadStatus = ""
            var fileName = ""
            id?.let {
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                if (cursor != null && cursor.moveToNext()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    fileName =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    downloadStatus = when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> "SUCCESS"
                        else -> "FAILURE"
                    }
                    cursor.close()
                }
            }
            setUpNotification(
                CHANNEL_ID,
                getString(R.string.notification_channel_name)
            )
            val mutableSet = mutableSetOf(
                DownloadDetails(
                    DOWNLOAD_STATUS,
                    downloadStatus
                ),
                DownloadDetails(
                    DOWNLOAD_URI,
                    fileName
                )
            )
            notificationManager.sendNotification(
                mutableSet,
                applicationContext
            )
            custom_button.reset()
            raddioGroup.clearCheck()
        }
    }

    private fun download() {
        var url = ""
        var title = ""

        if (raddioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, getString(R.string.button_info_text), Toast.LENGTH_SHORT).show()
            custom_button.reset()
        } else {
            when (raddioGroup.checkedRadioButtonId) {
                R.id.optionOne -> {
                    url = URL_OPTION_ONE
                    title = getString(R.string.option_one)
                }
                R.id.optionTwo -> {
                    url = URL_OPTION_TWO
                    title = getString(R.string.option_two)
                }
                R.id.optionThree -> {
                    title = getString(R.string.option_three)
                    url = URL_OPTION_THREE
                }
            }
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(title)
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        }

    }

    fun setUpNotification(channelId: String, channelName: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notification = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notification)
        }
    }

    companion object {
        private const val URL_OPTION_TWO =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_OPTION_ONE = "https://github.com/bumptech/glide"
        private const val URL_OPTION_THREE = "https://github.com/square/retrofit"
        const val CHANNEL_ID = "channelId"
    }

}
