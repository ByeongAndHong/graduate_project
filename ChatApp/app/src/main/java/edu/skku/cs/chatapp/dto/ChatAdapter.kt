package edu.skku.cs.chatapp.dto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.skku.cs.chatapp.R

data class SendMessage(var UserId: Int?, var TargetId: Int?, var Message: String)

data class SendMessageResponse(var Status:String)

data class Message(var UserId: Int, var Message: String)

data class UpdateResponse(var Status: String, var Messages: List<Message>)

class ChatAdapter(val context: Context, val items: List<Message>, val id: String): BaseAdapter() {
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
        val view: View = inflater.inflate(R.layout.item_message, null)

        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        val myMessageTextView = view.findViewById<TextView>(R.id.myMessageTextView)

        if(id == items.get(p0).UserId.toString()){
            messageTextView.visibility = View.GONE
            myMessageTextView.text = items.get(p0).Message
        }
        else{
            myMessageTextView.visibility = View.GONE
            messageTextView.text = items.get(p0).Message
        }

        return view
    }
}