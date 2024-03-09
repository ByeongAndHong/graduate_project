package edu.skku.cs.chatapp.dto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import edu.skku.cs.chatapp.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.AccessController.getContext

data class Friend(var ChatId: Int, var Id: Int, var UserName: String, var Email: String, var Image: ByteArray?)
data class FriendListResponse(var Status: String, var Friends: List<Friend>)

class FriendListAdapter(val context:Context, val bundle: Bundle?, val items: List<Friend>, val id: String): BaseAdapter() {
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
        val view: View = inflater.inflate(R.layout.item_friend, null)

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val plusImageView = view.findViewById<ImageView>(R.id.plusImageView)
        plusImageView.visibility = View.GONE

        nameTextView.text = items.get(p0).UserName

        view.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(context, ChatActivity::class.java).apply{
                    putExtra(Utils.EXT_ID, id)
                    putExtra(Utils.EXT_FRIEND_NAME, items.get(p0).UserName)
                    putExtra(Utils.EXT_FRIEND_ID, items.get(p0).Id.toString())
                    putExtra(Utils.EXT_CHAT_ID, items.get(p0).ChatId.toString())
                }
                startActivity(context, intent, bundle)
            }
        }

        return view
    }
}