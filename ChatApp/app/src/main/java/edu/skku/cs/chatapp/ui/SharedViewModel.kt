package edu.skku.cs.chatapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.skku.cs.chatapp.dto.Friend

class SharedViewModel : ViewModel() {
    private val userIdLiveData = MutableLiveData<String>()
    private val friendListLiveData = MutableLiveData<List<Friend>>()

    fun setUserId(userId: String) {
        userIdLiveData.value = userId
    }

    fun setFriendList(friendList: List<Friend>){
        friendListLiveData.value = friendList
    }

    fun getUserId(): LiveData<String> {
        return userIdLiveData
    }

    fun getFriendList(): LiveData<List<Friend>>{
        return friendListLiveData
    }
}