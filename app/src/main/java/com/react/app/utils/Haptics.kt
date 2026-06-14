package com.react.app.utils

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View

fun Context.triggerHapticAndSound() {
    val v = View(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    } else {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
}
