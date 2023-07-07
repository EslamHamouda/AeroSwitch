package com.eslam.aeroswitch

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class AirplaneModeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val minutes = inputData.getLong(KEY_MINUTES, 0L)
        enableAirplaneMode()

        delay(minutes * 60 * 1000) // Delay in milliseconds

        disableAirplaneMode()
        return Result.success()
    }

    private fun enableAirplaneMode() {
        Settings.Global.putInt(applicationContext.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 1)
        sendAirplaneModeBroadcast(true)
    }

    private fun disableAirplaneMode() {
        Settings.Global.putInt(applicationContext.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
        sendAirplaneModeBroadcast(false)
    }

    private fun sendAirplaneModeBroadcast(isEnabled: Boolean) {
        val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        intent.putExtra("state", isEnabled)
        applicationContext.sendBroadcast(intent)
    }

    companion object {
        const val KEY_MINUTES = "minutes"
    }
}
