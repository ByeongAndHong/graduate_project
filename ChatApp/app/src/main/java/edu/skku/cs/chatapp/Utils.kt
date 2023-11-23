package edu.skku.cs.chatapp

import android.content.Context

class Utils {
    fun dpToPx(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}