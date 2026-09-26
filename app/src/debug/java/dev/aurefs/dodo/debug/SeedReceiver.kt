package dev.aurefs.dodo.debug

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.aurefs.dodo.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reset = intent.getBooleanExtra("reset", false)
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = MockData.seed(AppDatabase.getDatabase(context), reset)
                pending.setResult(Activity.RESULT_OK, result, null)
            } catch (e: Exception) {
                pending.setResult(Activity.RESULT_CANCELED, "Seeding failed: ${e.message}", null)
            } finally {
                pending.finish()
            }
        }
    }
}
