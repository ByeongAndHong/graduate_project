package edu.skku.cs.chatapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import edu.skku.cs.chatapp.databinding.ActivityMainBinding
import edu.skku.cs.chatapp.Utils
import edu.skku.cs.chatapp.dto.*
import edu.skku.cs.chatapp.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var utils = Utils()
    private var id:String? = "-1"
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra(Utils.EXT_ID)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.setUserId(id+"")
        sendFriendGetRequest()
        // 코루틴을 사용하여 메시지 전송 시작
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                // 메시지 전송 로직
                sendChatlistGetRequest()

                // 일정 시간 대기 후 다음 전송 시도
                delay(5000) // 예시: 5초마다 전송
            }
        }
        sendChatlistGetRequest()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_friendlist, R.id.navigation_chatlist, R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //프로젝트 처음 만들 면 생성되어 있는 액션 바 제거
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    private fun sendFriendGetRequest(){
        val client = OkHttpClient()
        val host = Utils.SERVER_URL

        val path = "/friends/" + id
        val req = Request.Builder().url(host+path).get().build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful)throw IOException("Unexpected code $response")
                    val str = response.body!!.string()
                    Log.d("response", str)
                    val data = Gson().fromJson(str, FriendListResponse::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(data.Status == "success"){
                            val friendList = mutableListOf<Friend>()
                            val friendsArray = data.Friends
                            for (friendData in friendsArray) {
                                // Assuming 'image' is a byte array
                                //val bitmap = BitmapFactory.decodeByteArray(friendData.image, 0, friendData.image.size)
                                val friend = Friend(friendData.ChatId, friendData.Id, friendData.UserName, friendData.Email, friendData.Image)
                                friendList.add(friend)
                            }

                            sharedViewModel.setFriendList(friendList)
                        }
                        else{
                            Toast.makeText(this@MainActivity, "정보 로딩 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun sendChatlistGetRequest(){
        val client = OkHttpClient()
        val host = Utils.SERVER_URL

        val path = "/chatlist/" + id
        val req = Request.Builder().url(host+path).get().build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful)throw IOException("Unexpected code $response")
                    val str = response.body!!.string()
                    Log.d("response", str)
                    val data = Gson().fromJson(str, ChatListItemResponse::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(data.Status == "success"){
                            val chatList = mutableListOf<ChatListItem>()
                            val chatArray = data.Chats
                            for (chatData in chatArray) {
                                val chat = ChatListItem(chatData.ChatId, chatData.UserId, chatData.UserName, chatData.Message)
                                chatList.add(chat)
                            }

                            sharedViewModel.setChatList(chatList)
                        }
                        else{
                            Toast.makeText(this@MainActivity, "정보 로딩 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}