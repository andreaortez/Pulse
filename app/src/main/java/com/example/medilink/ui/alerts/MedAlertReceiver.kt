package com.example.medilink.ui.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class MedAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras: Bundle? = intent.extras
        Log.d("MedAlertReceiver", "onReceive DISPARADO con extras=$extras")

        val activityIntent = Intent(context, AlertActivity::class.java).apply {
            if (extras != null) {
                putExtras(extras)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        context.startActivity(activityIntent)
    }
}
