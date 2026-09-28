package dev.aurefs.dodo.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import dev.aurefs.dodo.data.AppDatabase
import java.time.LocalDate

class ToggleEntryAction : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val doableId = parameters[doableIdKey] ?: return
        val date = parameters[epochDayKey]?.let(LocalDate::ofEpochDay) ?: return
        val done = parameters[doneKey] ?: return
        val entryDao = AppDatabase.getDatabase(context).doableEntryDao()

        if (date == LocalDate.now()) {
            entryDao.setDone(doableId, date, done)
        } else {
            entryDao.ensureDaysUpTo(LocalDate.now())
        }
        DoableWidget().updateAll(context)
    }

    companion object {
        val doableIdKey = ActionParameters.Key<Int>("doableId")
        val epochDayKey = ActionParameters.Key<Long>("epochDay")
        val doneKey = ActionParameters.Key<Boolean>("done")
    }
}
