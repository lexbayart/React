package com.react.app.utils

import android.content.Context
import android.content.Intent
import com.react.app.data.database.Log
import com.react.app.data.database.StateActionUsage
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

    fun exportStats(context: Context, logs: List<Log>, usages: List<StateActionUsage>) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(Date())

        val json = buildJsonString(logs, usages, timestamp)
        val csv = buildCsvString(logs, usages)

        val shareText = "React Stats Export ($timestamp)\n\n--- JSON ---\n\n$json\n\n--- CSV ---\n\n$csv"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "React Stats Export")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Export Stats"))
    }

    private fun buildJsonString(logs: List<Log>, usages: List<StateActionUsage>, timestamp: String): String {
        val root = JSONObject().apply {
            put("exportTimestamp", timestamp)
            put("appVersion", "1.0")
            put("logs", JSONArray().apply {
                for (log in logs) {
                    put(JSONObject().apply {
                        put("id", log.id)
                        put("timestamp", log.timestamp)
                        put("state_id", log.state_id)
                        put("action_id", log.action_id)
                    })
                }
            })
            put("stateActionUsage", JSONArray().apply {
                for (usage in usages) {
                    put(JSONObject().apply {
                        put("state_id", usage.state_id)
                        put("action_id", usage.action_id)
                        put("uses", usage.uses)
                    })
                }
            })
        }
        return root.toString(2)
    }

    private fun buildCsvString(logs: List<Log>, usages: List<StateActionUsage>): String {
        val sb = StringBuilder()
        sb.append("TYPE,id,timestamp,state_id,action_id,uses\n")

        for (log in logs) {
            sb.append("LOG,${log.id},${log.timestamp},${log.state_id},${log.action_id},\n")
        }

        for (usage in usages) {
            sb.append("USAGE,${usage.state_id}_${usage.action_id},,${usage.state_id},${usage.action_id},${usage.uses}\n")
        }

        return sb.toString()
    }
}
