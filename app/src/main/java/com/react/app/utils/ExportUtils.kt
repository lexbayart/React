package com.react.app.utils

import android.content.Context
import android.content.Intent
import com.react.app.data.database.Log
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

    fun exportStats(context: Context, logs: List<Log>, @Suppress("UNUSED_PARAMETER") usages: List<Nothing> = emptyList()) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(Date())

        val json = buildJsonString(logs, timestamp)
        val csv = buildCsvString(logs)

        val shareText = "React Stats Export ($timestamp)\n\n--- JSON ---\n\n$json\n\n--- CSV ---\n\n$csv"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "React Stats Export")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Export Stats"))
    }

    private fun buildJsonString(logs: List<Log>, timestamp: String): String {
        val root = JSONObject().apply {
            put("exportTimestamp", timestamp)
            put("appVersion", "2.0")
            put("logs", JSONArray().apply {
                for (log in logs) {
                    put(JSONObject().apply {
                        put("id", log.id)
                        put("timestamp", log.timestamp)
                        put("action_id", log.action_id)
                    })
                }
            })
        }
        return root.toString(2)
    }

    private fun buildCsvString(logs: List<Log>): String {
        val sb = StringBuilder()
        sb.append("TYPE,id,timestamp,action_id\n")

        for (log in logs) {
            sb.append("LOG,${log.id},${log.timestamp},${log.action_id}\n")
        }

        return sb.toString()
    }
}
