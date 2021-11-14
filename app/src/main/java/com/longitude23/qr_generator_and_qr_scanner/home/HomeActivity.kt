package com.longitude23.qr_generator_and_qr_scanner.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.longitude23.qr_generator_and_qr_scanner.databinding.ActivityHomeBinding
import com.longitude23.qr_generator_and_qr_scanner.qrCodeGenerate.QRCodeGeneratorActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        binding.qrCodeGeneratorCardViewId.setOnClickListener() {
            this.startActivity(Intent(this, QRCodeGeneratorActivity::class.java))

        }

        binding.qrCodeScannerCardViewId.setOnClickListener() {

        }

    }
}