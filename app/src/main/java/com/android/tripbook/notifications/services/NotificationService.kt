package com.android.tripbook.notifications.services

import android.content.Context
import android.util.Log
import com.android.tripbook.notifications.config.EmailConfig
import com.android.tripbook.notifications.managers.NotificationTemplateManager
import com.android.tripbook.notifications.models.*
import kotlinx.coroutines.*

class NotificationService(private val context: Context) {
    private val TAG = "NotificationService"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Services
    private val templateManager = NotificationTemplateManager()
    private val pushService = PushNotificationService(context)

    // Logs des notifications
    private val notificationLogs = mutableListOf<NotificationLog>()

    /**
     *  FONCTION PRINCIPALE - Point d'entrée pour toutes les notifications
     */
    suspend fun processNotification(request: NotificationRequest) {
        try {
            Log.d(TAG, "📨 Processing notification: ${request.type} for user: ${request.userId}")

            // 1. Récupérer le profil utilisateur
            val userProfile = getUserProfile(request.userId)
            if (userProfile == null) {
                Log.e(TAG, " User profile not found for userId: ${request.userId}")
                return
            }

            // 2. Générer le template de notification
            val template = templateManager.getTemplate(request.type, request.data)

            // 3. Envoyer selon les préférences utilisateur
            val jobs = mutableListOf<Job>()

            // Notification push locale
            if (userProfile.notificationPreferences.pushEnabled) {
                jobs.add(serviceScope.launch {
                    val success = pushService.sendLocalNotification(userProfile, template, request)
                    logNotification(
                        userProfile.userId,
                        request.type,
                        if (success) NotificationStatus.SENT else NotificationStatus.FAILED
                    )
                })
            }

            // Email (optionnel - à activer plus tard)
            if (userProfile.notificationPreferences.emailEnabled) {
                jobs.add(serviceScope.launch {
                    // TODO: Implémenter l'envoi d'email plus tard
                    Log.d(TAG, "📧 Email notification would be sent to: ${userProfile.email}")
                    logNotification(userProfile.userId, request.type, NotificationStatus.SENT)
                })
            }

            // Attendre que tout soit envoyé
            jobs.joinAll()

            Log.d(TAG, "✅ Notification processing completed for user: ${request.userId}")

        } catch (e: Exception) {
            Log.e(TAG, "💥 Error processing notification", e)
            logNotification(request.userId, request.type, NotificationStatus.FAILED, e.message)
        }
    }

    /**
     * 📦 Traite plusieurs notifications en lot
     */
    suspend fun processBatchNotifications(requests: List<NotificationRequest>) {
        Log.d(TAG, "📦 Processing ${requests.size} notifications in batch")

        requests.chunked(5).forEach { batch ->
            val jobs = batch.map { request ->
                serviceScope.launch {
                    processNotification(request)
                }
            }
            jobs.joinAll()
            delay(500) // Petite pause entre les lots
        }
    }

    /**
     * ⏰ Planifier un rappel automatique
     */
    fun scheduleReminder(userId: String, tripDate: Long, tripDetails: Map<String, Any>) {
        val reminderTime = tripDate - (24 * 60 * 60 * 1000) // 24h avant le voyage

        if (reminderTime > System.currentTimeMillis()) {
            serviceScope.launch {
                val delayTime = reminderTime - System.currentTimeMillis()
                Log.d(TAG, "⏰ Scheduling reminder in ${delayTime / (1000 * 60 * 60)} hours")

                delay(delayTime)

                val reminderRequest = NotificationRequest(
                    userId = userId,
                    type = NotificationType.TRIP_REMINDER,
                    data = tripDetails
                )

                processNotification(reminderRequest)
            }
        }
    }

    /**
     * 👤 Récupère le profil utilisateur (à connecter avec votre vraie base de données)
     */
    private suspend fun getUserProfile(userId: String): UserProfile? {
        // TODO: Connecter avec votre vraie base de données
        // Pour l'instant, on simule
        return try {
            UserProfile(
                userId = userId,
                email = "user$userId@tripbook.com",
                fcmToken = null, // Pour les notifications locales, pas besoin de FCM token
                notificationPreferences = NotificationPreferences(
                    pushEnabled = true,
                    emailEnabled = true,
                    reminderEnabled = true
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user profile", e)
            null
        }
    }

    /**
     * 📝 Enregistre les logs de notifications
     */
    private fun logNotification(
        userId: String,
        type: NotificationType,
        status: NotificationStatus,
        error: String? = null
    ) {
        val log = NotificationLog(
            userId = userId,
            type = type,
            status = status,
            error = error
        )

        notificationLogs.add(log)
        Log.d(TAG, "📝 Logged notification: $type -> $status for user: $userId")

        // TODO: Sauvegarder en base de données si nécessaire
    }

    /**
     * 📊 Récupère les logs pour un utilisateur
     */
    fun getLogsForUser(userId: String): List<NotificationLog> {
        return notificationLogs.filter { it.userId == userId }
    }

    /**
     * 🧹 Nettoie le service
     */
    fun cleanup() {
        serviceScope.cancel()
    }
}
