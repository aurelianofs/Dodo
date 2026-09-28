package dev.aurefs.dodo.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.aurefs.dodo.data.AppDatabase
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MidnightRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        AppDatabase.getDatabase(applicationContext).doableEntryDao().ensureDaysUpTo(LocalDate.now())
        DoableWidget().updateAll(applicationContext)
        schedule(applicationContext, ExistingWorkPolicy.APPEND_OR_REPLACE)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "midnight-widget-refresh"

        fun schedule(context: Context, policy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP) {
            val now = LocalDateTime.now()
            val nextRun = now.toLocalDate().plusDays(1).atStartOfDay().plusMinutes(1)
            val request = OneTimeWorkRequestBuilder<MidnightRefreshWorker>()
                .setInitialDelay(Duration.between(now, nextRun))
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, policy, request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
