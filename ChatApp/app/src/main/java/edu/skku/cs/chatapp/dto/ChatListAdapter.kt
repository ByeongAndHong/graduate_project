package edu.skku.cs.chatapp.dto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import edu.skku.cs.chatapp.ChatActivity
import edu.skku.cs.chatapp.R
import edu.skku.cs.chatapp.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ChatListItem(var ChatId: Int, var UserId: Int, var UserName: String, var Message: String)

data class ChatListItemResponse(var Status:String, var Chats: List<ChatListItem>)

class ChatListAdapter(val context: Context, val bundle: Bundle?, val items: List<ChatListItem>, val id: String): BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item_chat, null)

        val friendImageView = view.findViewById<ImageView>(R.id.friendImageView)
        val friendTextView = view.findViewById<TextView>(R.id.friendTextView)
        val latestMessageTextView = view.findViewById<TextView>(R.id.latestMessageTextView)

        friendTextView.text = items.get(p0).UserName
        latestMessageTextView.text = items.get(p0).Message

        view.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(context, ChatActivity::class.java).apply{
                    putExtra(Utils.EXT_ID, id)
                    putExtra(Utils.EXT_FRIEND_NAME, items.get(p0).UserName)
                    putExtra(Utils.EXT_FRIEND_ID, items.get(p0).UserId.toString())
                    putExtra(Utils.EXT_CHAT_ID, items.get(p0).ChatId.toString())
                }
                ContextCompat.startActivity(context, intent, bundle)
            }
        }

        return view
    }
}