package com.example.lovepoly

import android.content.Intent
import android.os.Bundle
import android.view.View
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
            // Hiển thị nút reset khi vào level
            binding.resetButton.visibility = View.VISIBLE
        }

        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Thêm xử lý sự kiện cho nút reset
        binding.resetButton.setOnClickListener {
            binding.glSurfaceView.resetRotation()
        }

        // Xử lý khi quay lại màn hình chính
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                // Ẩn nút reset khi quay lại màn hình chính
                binding.resetButton.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.glSurfaceView.onResume()
    }
}
