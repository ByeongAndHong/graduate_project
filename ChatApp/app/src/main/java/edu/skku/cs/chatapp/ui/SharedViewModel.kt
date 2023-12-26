package edu.skku.cs.chatapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.skku.cs.chatapp.dto.ChatListItem
import edu.skku.cs.chatapp.dto.Friend

class SharedViewModel : ViewModel() {
    private val userIdLiveData = MutableLiveData<String>()
    private val friendListLiveData = MutableLiveData<List<Friend>>()
    private val chatListLiveData = MutableLiveData<List<ChatListItem>>()

    fun setUserId(userId: String) {
        userIdLiveData.value = userId
    }

    fun setFriendList(friendList: List<Friend>){
        friendListLiveData.value = friendList
    }

    fun setChatList(chatList: List<ChatListItem>){
        chatListLiveData.value = chatList
    }

    fun getUserId(): LiveData<String> {
        return userIdLiveData
    }

    fun getFriendList(): LiveData<List<Friend>>{
        return friendListLiveData
    }

    fun getChatList(): LiveData<List<ChatListItem>>{
        return chatListLiveData
    }
}