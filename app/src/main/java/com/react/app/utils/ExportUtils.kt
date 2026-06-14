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

        val jsonIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, "React Stats Export")
            putExtra(Intent.EXTRA_TEXT, "Stats exported from React app")
            putExtra(Intent.EXTRA_STREAM, createTempFile(context, "react_stats_$timestamp.json", json))
        }

        val csvIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "React Stats Export CSV")
            putExtra(Intent.EXTRA_TEXT, "Stats exported from React app")
            putExtra(Intent.EXTRA_STREAM, createTempFile(context, "react_stats_$timestamp.csv", csv))
        }

        val shareIntent = Intent.createChooser(jsonIntent, "Export Stats")
        val shareIntentCsv = Intent.createChooser(csvIntent, "Export Stats")

        context.startActivity(shareIntent)
        context.startActivity(shareIntentCsv)
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

    private fun createTempFile(context: Context, filename: String, content: String): android.net.Uri {
        val file = java.io.File(context.cacheDir, filename)
        file.writeText(content)
        return android.provider.MediaStore.Files.getContentUri("external_cache")
    }
}
