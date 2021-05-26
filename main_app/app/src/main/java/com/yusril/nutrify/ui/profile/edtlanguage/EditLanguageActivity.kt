package com.yusril.nutrify.ui.profile.edtlanguage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.ActivityEditLanguageBinding

class EditLanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditLanguageBinding

    override fun onResume() {
        super.onResume()
        val languageList: Array<String> = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this,R.layout.drop_down_item, languageList)
        binding.inputLanguage.setAdapter(adapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}