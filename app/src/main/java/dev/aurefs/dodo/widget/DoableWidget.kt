package dev.aurefs.dodo.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import dev.aurefs.dodo.data.AppDatabase
import kotlinx.coroutines.flow.first

class DoableWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).doableDao()
        val doables = dao.getActiveDoables().first()

        provideContent {
            Column(modifier = androidx.glance.GlanceModifier.fillMaxSize().padding(12.dp)) {
                Text(text = "Dodo")
                doables.forEach { doable ->
                    Text(text = "• ${doable.title}")
                }
            }
        }
    }
}