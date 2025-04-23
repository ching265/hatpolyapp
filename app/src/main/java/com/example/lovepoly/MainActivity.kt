package com.example.lovepoly

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.lovepoly.databinding.ActivityMainBinding
import com.example.lovepoly.ui.level.Level1Fragment
import com.example.lovepoly.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLevel1.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Level1Fragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
