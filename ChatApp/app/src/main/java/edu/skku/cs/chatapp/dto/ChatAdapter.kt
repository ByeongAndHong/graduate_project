package edu.skku.cs.chatapp.dto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import edu.skku.cs.chatapp.R
import edu.skku.cs.chatapp.Utils

data class SendMessage(var UserId: Int?, var TargetId: Int?, var Message: String)

data class SendMessageResponse(var Status:String, var ChatId: Int)

data class Message(var UserId: Int, var Message: String)

data class UpdateResponse(var Status: String, var Messages: List<Message>)

data class EmotionResponse(var Argmax: Int, var Percent0: Int, var Percent1: Int, var Percent2: Int, var Percent3: Int, var Analysis: String)

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
        val myMessageImageView = view.findViewById<ImageView>(R.id.myMessageImageView)
        val messageImageView = view.findViewById<ImageView>(R.id.messageImageView)

        val lineLength = 19
        val stringBuilder = StringBuilder()
        if(id == items.get(p0).UserId.toString()){
            messageTextView.visibility = View.GONE
            messageImageView.visibility = View.GONE

            val myMessage = items.get(p0).Message
            val layoutParams = myMessageImageView.layoutParams
            val lineHeight = layoutParams.height // 줄 수에 따라 이미지 높이 조절
            for (i in myMessage.indices) {
                stringBuilder.append(myMessage[i])
                if ((i + 1) % lineLength == 0 && i != myMessage.length - 1) {
                    stringBuilder.append("\n")
                }
            }
            val formattedMessage = stringBuilder.toString()
            val count = formattedMessage.count { it == '\n' }
            layoutParams.height = lineHeight * (count+1)
            myMessageImageView.layoutParams = layoutParams
            myMessageTextView.text = formattedMessage
        }
        else{
            myMessageTextView.visibility = View.GONE
            myMessageImageView.visibility = View.GONE

            val message = items.get(p0).Message
            val layoutParams = messageImageView.layoutParams
            val lineHeight = layoutParams.height
            for (i in message.indices) {
                stringBuilder.append(message[i])
                if ((i + 1) % lineLength == 0 && i != message.length - 1) {
                    stringBuilder.append("\n")
                }
            }
            val formattedMessage = stringBuilder.toString()
            val count = formattedMessage.count { it == '\n' }
            layoutParams.height = lineHeight * (count+1)
            messageImageView.layoutParams = layoutParams
            messageTextView.text = formattedMessage
        }

        return view
    }
}