package edu.skku.cs.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.gson.Gson
import edu.skku.cs.chatapp.dto.RegisterUser
import edu.skku.cs.chatapp.dto.RegisterUserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val editTextPersonName = findViewById<EditText>(R.id.editTextPersonName)
            val editTextEmailAddress = findViewById<EditText>(R.id.editTextEmailAddress)
            val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

            val name = editTextPersonName.text.toString()
            val email = editTextEmailAddress.text.toString()
            val password = editTextPassword.text.toString()

            sendRegisterRequest(name, email, password)
        }

        //프로젝트 처음 만들 면 생성되어 있는 액션 바 제거
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    private fun sendRegisterRequest(name: String, email: String, password: String) {
        val json = Gson().toJson(RegisterUser(name, email, password))
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val host = Utils.SERVER_URL

        val path = "/register"
        val req = Request.Builder().url(host+path).post(json.toString().toRequestBody(mediaType)).build()

        client.newCall(req).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful)throw IOException("Unexpected code $response")
                    val str = response.body!!.string()
                    val data = Gson().fromJson(str, RegisterUserResponse::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(data.Status == "success"){
                            val intent = Intent(applicationContext, StartActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this@RegisterActivity, "Wrong User Name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}