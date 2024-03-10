package edu.skku.cs.chatapp

import android.content.Context

class Utils {
    companion object {
        const val EXT_ID = "id"
        const val EXT_FRIEND_NAME = "friendName"
        const val EXT_FRIEND_ID = "friendId"
        const val EXT_CHAT_ID = "chatId"
        const val SERVER_URL = "http://172.20.10.2:5000"
        const val APP_NAME = "어플 이름"
    }
    fun dpToPx(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}