package edu.skku.cs.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.gson.Gson
import edu.skku.cs.chatapp.dto.LoginUser
import edu.skku.cs.chatapp.dto.LoginUserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    companion object{
        const val EXT_ID = "id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val editTextEmailAddress = findViewById<EditText>(R.id.editTextEmailAddress)
            val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

            val email = editTextEmailAddress.text.toString()
            val password = editTextPassword.text.toString()

            sendLoginRequest(email, password)
        }

        //프로젝트 처음 만들 면 생성되어 있는 액션 바 제거
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    private fun sendLoginRequest(email: String, password: String) {
        val json = Gson().toJson(LoginUser(email, password))
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val host = "http://192.168.1.101:5000"

        val path = "/login"
        val req = Request.Builder().url(host+path).post(json.toString().toRequestBody(mediaType)).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful)throw IOException("Unexpected code $response")
                    val str = response.body!!.string()
                    val data = Gson().fromJson(str, LoginUserResponse::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(data.Status == "success"){
                            val intent = Intent(applicationContext, MainActivity::class.java).apply{
                                putExtra(EXT_ID, data.Id.toString())
                            }
                            startActivity(intent)
                            finish()
                        }
                        else if(data.Status == "wrong id"){
                            Toast.makeText(this@LoginActivity, "아이디를 확인해 주세요", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this@LoginActivity, "비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}