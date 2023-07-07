package pers.shawxingkwok.androidutil.view.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pers.shawxingkwok.androidutil.view.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}