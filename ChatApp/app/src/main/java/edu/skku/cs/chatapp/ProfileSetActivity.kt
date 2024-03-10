package edu.skku.cs.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.ActionBar

class ProfileSetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_set)

        val xButton = findViewById<ImageView>(R.id.xImageView)
        xButton.setOnClickListener {
            finish()
        }

        //프로젝트 처음 만들 면 생성되어 있는 액션 바 제거
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }
}