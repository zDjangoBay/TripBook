package com.android.tripbook.notifications.managers

import com.android.tripbook.notifications.models.NotificationTemplate
import com.android.tripbook.notifications.models.NotificationType

class NotificationTemplateManager {

    fun getTemplate(type: NotificationType, data: Map<String, Any>): NotificationTemplate {
        return when (type) {
            NotificationType.BOOKING_CONFIRMED -> {
                val destination = data["destination"] as? String ?: "votre destination"
                val date = data["date"] as? String ?: "bientôt"

                NotificationTemplate(
                    title = "Réservation confirmée !",
                    body = "Votre voyage vers $destination est confirmé pour le $date",
                    emailSubject = "Confirmation de votre réservation TripBook",
                    emailBody = createBookingEmail(destination, date, data)
                )
            }

            NotificationType.PAYMENT_SUCCESS -> {
                val amount = data["amount"] as? String ?: "0€"

                NotificationTemplate(
                    title = "Paiement réussi",
                    body = "Votre paiement de $amount a été traité avec succès",
                    emailSubject = "Confirmation de paiement - TripBook",
                    emailBody = createPaymentEmail(amount)
                )
            }

            NotificationType.TRIP_REMINDER -> {
                val destination = data["destination"] as? String ?: "votre destination"

                NotificationTemplate(
                    title = "Rappel de voyage",
                    body = "N'oubliez pas votre voyage vers $destination demain !",
                    emailSubject = "Rappel : Votre voyage approche",
                    emailBody = createReminderEmail(destination)
                )
            }

            NotificationType.BOOKING_MODIFIED -> {
                NotificationTemplate(
                    title = "Réservation modifiée",
                    body = "Votre réservation a été mise à jour avec succès",
                    emailSubject = "Modification de votre réservation",
                    emailBody = createModificationEmail()
                )
            }

            NotificationType.BOOKING_CANCELLED -> {
                NotificationTemplate(
                    title = "Réservation annulée",
                    body = "Votre réservation a été annulée. Remboursement en cours.",
                    emailSubject = "Annulation de votre réservation",
                    emailBody = createCancellationEmail()
                )
            }

            NotificationType.REFUND_PROCESSED -> {
                val amount = data["amount"] as? String ?: "0€"

                NotificationTemplate(
                    title = "Remboursement traité",
                    body = "Votre remboursement de $amount a été traité",
                    emailSubject = "Remboursement effectué - TripBook",
                    emailBody = createRefundEmail(amount)
                )
            }
        }
    }

    private fun createBookingEmail(destination: String, date: String, data: Map<String, Any>): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #2E86AB;">✅ Réservation confirmée</h2>
                <p>Bonjour,</p>
                <p>Votre réservation TripBook a été confirmée avec succès !</p>
                
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0;">
                    <h3>📍 Détails du voyage</h3>
                    <p><strong>Destination :</strong> $destination</p>
                    <p><strong>Date :</strong> $date</p>
                </div>
                
                <p>Bon voyage avec TripBook ! 🌍</p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun createPaymentEmail(amount: String): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #28a745;">💳 Paiement confirmé</h2>
                <p>Votre paiement de <strong>$amount</strong> a été traité avec succès.</p>
                <p>Merci pour votre confiance ! 🙏</p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun createReminderEmail(destination: String): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #ffc107;">⏰ Rappel de voyage</h2>
                <p>Votre voyage vers <strong>$destination</strong> est prévu pour demain !</p>
                <p>N'oubliez pas vos documents de voyage ! 📄</p>
                <p>Bon voyage ! ✈️</p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun createModificationEmail(): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #17a2b8;">✏️ Réservation modifiée</h2>
                <p>Votre réservation a été mise à jour avec succès.</p>
                <p>Consultez l'application pour voir les détails.</p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun createCancellationEmail(): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #dc3545;">❌ Réservation annulée</h2>
                <p>Votre réservation a été annulée comme demandé.</p>
                <p>Le remboursement sera traité dans les prochains jours.</p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun createRefundEmail(amount: String): String {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #28a745;">💰 Remboursement effectué</h2>
                <p>Votre remboursement de <strong>$amount</strong> a été traité.</p>
                <p>Les fonds apparaîtront sur votre compte dans 3-5 jours ouvrables.</p>
            </body>
            </html>
        """.trimIndent()
    }
}