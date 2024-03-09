package edu.skku.cs.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.gson.Gson
import edu.skku.cs.chatapp.dto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    var messageList = mutableListOf<Message>()
    var updateFlag = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val id = intent.getStringExtra(Utils.EXT_ID)?.toInt()
        val friendName = intent.getStringExtra(Utils.EXT_FRIEND_NAME)
        val friendId = intent.getStringExtra(Utils.EXT_FRIEND_ID)?.toInt()
        var chatId = intent.getStringExtra(Utils.EXT_CHAT_ID)

        Log.d("debug", chatId.toString())

        //프로젝트 처음 만들 면 생성되어 있는 액션 바 제거
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }

        val friendNameTextView = findViewById<TextView>(R.id.friendNameTextView)
        val xButton = findViewById<ImageView>(R.id.xImageView)
        val messageText = findViewById<EditText>(R.id.messageText)
        val sendImageView = findViewById<ImageView>(R.id.sendImageView)

        friendNameTextView.text = friendName
        xButton.setOnClickListener {
            finish()
        }

        sendImageView.setOnClickListener {
            val sendMessage = messageText.text.toString()

            if(sendMessage.length == 0){
                Toast.makeText(this@ChatActivity, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                val json = Gson().toJson(SendMessage(id, friendId, sendMessage))
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val client = OkHttpClient()
                val host = Utils.SERVER_URL

                val path = "/send"
                val req = Request.Builder().url(host+path).post(json.toString().toRequestBody(mediaType)).build()

                client.newCall(req).enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if(!response.isSuccessful)throw IOException("Unexpected code $response")
                            val str = response.body!!.string()
                            val data = Gson().fromJson(str, SendMessageResponse::class.java)
                            CoroutineScope(Dispatchers.Main).launch {
                                if(data.Status == "success"){
                                    messageText.setText("")
                                }
                                else{
                                    Toast.makeText(this@ChatActivity, "메시지 전송 실패", Toast.LENGTH_SHORT).show()
                                }

                                if(chatId == "-1"){
                                    chatId = data.ChatId.toString()
                                }
                            }
                        }
                    }
                })
            }
        }

        val chatAdapter = ChatAdapter(this@ChatActivity, messageList, id.toString())
        val listView = findViewById<ListView>(R.id.messageListView)
        listView.adapter = chatAdapter

        CoroutineScope(Dispatchers.IO).launch{
            while(updateFlag){
                val client = OkHttpClient()
                val host = Utils.SERVER_URL

                val path = "/message/" + chatId
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
                            val data = Gson().fromJson(str, UpdateResponse::class.java)
                            CoroutineScope(Dispatchers.Main).launch {
                                if(data.Status == "success"){
                                    val newMessageList = mutableListOf<Message>()
                                    val messageArray = data.Messages
                                    for (messageData in messageArray) {
                                        val message = Message(messageData.UserId, messageData.Message)
                                        newMessageList.add(message)
                                    }
                                    // 중복을 제외한 새로운 메시지 필터링
                                    val uniqueNewMessages = newMessageList.filter { newMessage ->
                                        !messageList.any { existingMessage ->
                                            newMessage.UserId == existingMessage.UserId && newMessage.Message == existingMessage.Message
                                        }
                                    }

                                    // 중복을 제외한 새로운 메시지를 기존 메시지 리스트에 추가
                                    if(uniqueNewMessages.size > 0){
                                        messageList.addAll(uniqueNewMessages)
                                        chatAdapter.notifyDataSetChanged()
                                        listView.post {
                                            listView.setSelection(chatAdapter.count - 1)
                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(this@ChatActivity, "정보 로딩 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                })

                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        updateFlag = false
    }
}