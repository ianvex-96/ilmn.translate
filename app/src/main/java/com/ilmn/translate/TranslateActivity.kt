package com.ilmn.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.ilmn.translate.databinding.ActivityTranslateBinding

class TranslateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTranslateBinding
    private var translator: Translator? = null
    private var sourceText = ""
    
    private val languages = listOf(
        Pair("Otomatis", "auto"),
        Pair("Inggris", TranslateLanguage.ENGLISH),
        Pair("Indonesia", TranslateLanguage.INDONESIAN),
        Pair("Jepang", TranslateLanguage.JAPANESE),
        Pair("Korea", TranslateLanguage.KOREAN),
        Pair("Mandarin", TranslateLanguage.CHINESE),
        Pair("Arab", TranslateLanguage.ARABIC),
        Pair("Prancis", TranslateLanguage.FRENCH),
        Pair("Jerman", TranslateLanguage.GERMAN),
        Pair("Spanyol", TranslateLanguage.SPANISH)
    )
    
    private var fromLang = "auto"
    private var toLang = TranslateLanguage.INDONESIAN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get text from PROCESS_TEXT
        sourceText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""
        } else {
            ""
        }
        
        binding.sourceText.text = sourceText

        setupSpinners()
        setupButtons()
        
        if (sourceText.isNotEmpty()) {
            translateText()
        }
    }

    private fun setupSpinners() {
        val languageNames = languages.map { it.first }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerFrom.adapter = adapter
        binding.spinnerTo.adapter = adapter
        
        // Set default
        binding.spinnerFrom.setSelection(0) // Auto
        binding.spinnerTo.setSelection(2) // Indonesia
        
        binding.spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fromLang = languages[position].second
                if (sourceText.isNotEmpty() && position != 0) {
                    translateText()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        binding.spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toLang = languages[position].second
                if (sourceText.isNotEmpty() && toLang != "auto") {
                    translateText()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        binding.btnCopy.setOnClickListener {
            val text = binding.translatedText.text.toString()
            if (text.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("translated", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Tersalin", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnClose.setOnClickListener {
            finish()
        }
        
        binding.btnSwap.setOnClickListener {
            // Simple swap
            val tempFrom = binding.spinnerFrom.selectedItemPosition
            val tempTo = binding.spinnerTo.selectedItemPosition
            if (tempFrom != 0 && tempTo != 0) {
                binding.spinnerFrom.setSelection(tempTo)
                binding.spinnerTo.setSelection(tempFrom)
            }
        }
    }

    private fun translateText() {
        binding.progressBar.visibility = View.VISIBLE
        binding.translatedText.text = ""
        
        if (fromLang == "auto") {
            // Use language identification
            val languageId = com.google.mlkit.nl.languageid.LanguageIdentification.getClient()
            languageId.identifyLanguage(sourceText)
                .addOnSuccessListener { languageCode ->
                    if (languageCode == "und") {
                        binding.translatedText.text = sourceText
                        binding.progressBar.visibility = View.GONE
                        return@addOnSuccessListener
                    }
                    doTranslate(languageCode, toLang)
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    binding.translatedText.text = sourceText
                }
        } else {
            doTranslate(fromLang, toLang)
        }
    }

    private fun doTranslate(sourceLang: String, targetLang: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()
        
        translator?.close()
        translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().requireWifi().build()
        
        translator?.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator?.translate(sourceText)
                    .addOnSuccessListener { translatedText ->
                        binding.translatedText.text = translatedText
                        binding.progressBar.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        binding.translatedText.text = sourceText
                        binding.progressBar.visibility = View.GONE
                    }
            }
            .addOnFailureListener {
                binding.translatedText.text = sourceText
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        translator?.close()
    }
}
