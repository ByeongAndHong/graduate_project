package edu.skku.cs.chatapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.ActionBar

class ChatSetActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_set)

        sharedPreferences = getSharedPreferences("chat_settings", MODE_PRIVATE)

        val graphSwitch = findViewById<Switch>(R.id.graphSwitch)
        val analysisSwitch = findViewById<Switch>(R.id.analysisSwitch)

        // 이전에 저장된 상태를 로드합니다.
        graphSwitch.isChecked = sharedPreferences.getBoolean("graph_switch_state", false)
        analysisSwitch.isChecked = sharedPreferences.getBoolean("analysis_switch_state", false)

        // 스위치 상태 변경 시 SharedPreferences에 저장합니다.
        graphSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedPreferences.edit().putBoolean("graph_switch_state", isChecked).apply()
        }

        analysisSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedPreferences.edit().putBoolean("analysis_switch_state", isChecked).apply()
        }

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