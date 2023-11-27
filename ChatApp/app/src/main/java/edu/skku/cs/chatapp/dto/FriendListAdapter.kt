package edu.skku.cs.chatapp.dto

import android.app.Activity
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import edu.skku.cs.chatapp.R

data class Friend(var UserName: String, var Email: String, var Image: ByteArray?)
data class FriendListResponse(var Status: String, var Friends: List<Friend>)

class FriendListAdapter(val inflater: LayoutInflater, val items: List<Friend>): BaseAdapter() {
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
        val view: View = inflater.inflate(R.layout.item_friend, null)

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val plusImageView = view.findViewById<ImageView>(R.id.plusImageView)
        plusImageView.visibility = View.GONE

        nameTextView.text = items.get(p0).UserName

        return view
    }
}