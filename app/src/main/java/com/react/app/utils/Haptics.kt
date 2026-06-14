package com.react.app.utils

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View

fun View.triggerHapticAndSound() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    } else {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    playClickSound()
}

private fun View.playClickSound() {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
}
