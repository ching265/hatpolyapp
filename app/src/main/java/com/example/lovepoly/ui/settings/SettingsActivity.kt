package com.example.lovepoly.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lovepoly.R
import com.example.lovepoly.databinding.ActivitySettingsBinding
import com.example.lovepoly.data.PrefsManager

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = PrefsManager(this)
        val speed = prefs.getSpeed()

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val tvSpeed = findViewById<TextView>(R.id.tvSpeed)

        seekBar.progress = speed
        tvSpeed.text = "Tốc độ: $speed"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.saveSpeed(progress)
                tvSpeed.text = "Tốc độ: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}