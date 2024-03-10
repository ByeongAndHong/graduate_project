package edu.skku.cs.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val mainNameTextView = findViewById<TextView>(R.id.nameTextView)
        mainNameTextView.text = Utils.APP_NAME

        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        registerButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        //프로젝트 처음 만들 면 생성되어 있는 액션 바 제거
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }
}