package com.android.tripbook.translation

import android.content.Context
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TranslationService {
    private val languageIdentifier = LanguageIdentification.getClient()
    private val translators = mutableMapOf<String, Translator>()
    
    companion object {
        private val SUPPORTED_LANGUAGES = mapOf(
            "en" to "English",
            "fr" to "Français",
            "es" to "Español",
            "de" to "Deutsch",
            "it" to "Italiano",
            "pt" to "Português",
            "ru" to "Русский",
            "ja" to "日本語",
            "ko" to "한국어",
            "zh" to "中文",
            "ar" to "العربية"
        )
    }
    
    suspend fun detectLanguage(text: String): String? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener { languageCode ->
                    if (languageCode == "und") {
                        continuation.resume(null) // Language not detected
                    } else {
                        continuation.resume(languageCode)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): String = withContext(Dispatchers.IO) {
        val translatorKey = "${sourceLanguage}-${targetLanguage}"
        
        val translator = translators.getOrPut(translatorKey) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            Translation.getClient(options)
        }
        
        // Ensure model is downloaded
        suspendCancellableCoroutine<Unit> { continuation ->
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
                
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
        
        // Perform translation
        suspendCancellableCoroutine { continuation ->
            translator.translate(text)
                .addOnSuccessListener { translatedText ->
                    continuation.resume(translatedText)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    fun getSupportedLanguages(): Map<String, String> = SUPPORTED_LANGUAGES
    
    fun getLanguageName(languageCode: String): String {
        return SUPPORTED_LANGUAGES[languageCode] ?: languageCode
    }
    
    fun cleanup() {
        translators.values.forEach { translator ->
            translator.close()
        }
        translators.clear()
    }
}

