package com.android.tripbook.accessibility

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechService(
    private val context: Context
) {
    private var textToSpeech: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()
    
    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()
    
    init {
        initializeTextToSpeech()
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale.getDefault())
                    if (result == TextToSpeech.LANG_MISSING_DATA || 
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Fallback to English if default language is not supported
                        tts.setLanguage(Locale.ENGLISH)
                    }
                    
                    tts.setSpeechRate(_speechRate.value)
                    
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }
                        
                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                        
                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })
                    
                    _isInitialized.value = true
                }
            }
        }
    }
    
    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (_isInitialized.value && !text.isBlank()) {
            val utteranceId = System.currentTimeMillis().toString()
            textToSpeech?.speak(text, queueMode, null, utteranceId)
        }
    }
    
    fun speakPostContent(postContent: String, authorName: String) {
        val textToRead = "Post de $authorName: $postContent"
        speak(textToRead)
    }
    
    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }
    
    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.1f, 3.0f)
        textToSpeech?.setSpeechRate(_speechRate.value)
    }
    
    fun setLanguage(locale: Locale): Boolean {
        return textToSpeech?.let { tts ->
            val result = tts.setLanguage(locale)
            result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        } ?: false
    }
    
    fun getAvailableLanguages(): Set<Locale> {
        return textToSpeech?.availableLanguages ?: emptySet()
    }
    
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        _isInitialized.value = false
        _isSpeaking.value = false
    }
}

