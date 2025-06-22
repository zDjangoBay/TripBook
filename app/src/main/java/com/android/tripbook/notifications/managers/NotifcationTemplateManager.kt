package com.android.tripbook.notifications.managers

import com.android.tripbook.notifications.models.NotificationTemplate
import com.android.tripbook.notifications.models.NotificationType
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.android.tripbook.R


class NotificationTemplateManager(private val context: Context) {

    fun getTemplate(type: NotificationType, data: Map<String, Any>): NotificationTemplate {
        return when (type) {
            NotificationType.BOOKING_CONFIRMED -> {
                NotificationTemplate(
                    title = "Réservation confirmée !",
                    body = "Votre voyage est confirmé",
                    layoutResource = R.layout.notification_booking_confirmed,
                    expandedLayoutResource = R.layout.notification_booking_expanded,
                    data = data
                )
            }

            NotificationType.PAYMENT_SUCCESS -> {
                NotificationTemplate(
                    title = "Paiement réussi",
                    body = "Paiement traité avec succès",
                    layoutResource = R.layout.notification_payment_success,
                    expandedLayoutResource = R.layout.notification_payment_expanded,
                    data = data
                )
            }

            NotificationType.TRIP_REMINDER -> {
                NotificationTemplate(
                    title = "Rappel de voyage",
                    body = "Votre voyage est demain !",
                    layoutResource = R.layout.notification_trip_reminder,
                    expandedLayoutResource = null, // Pas de version étendue pour ce type
                    data = data
                )
            }

            NotificationType.BOOKING_MODIFIED -> {
                NotificationTemplate(
                    title = "Réservation modifiée",
                    body = "Votre réservation a été mise à jour",
                    layoutResource = R.layout.notification_booking_modified,
                    expandedLayoutResource = R.layout.notification_booking_modified_expanded,
                    data = data
                )
            }

            NotificationType.BOOKING_CANCELLED -> {
                NotificationTemplate(
                    title = "Réservation annulée",
                    body = "Votre réservation a été annulée",
                    layoutResource = R.layout.notification_booking_cancelled,
                    expandedLayoutResource = R.layout.notification_booking_cancelled_expanded,
                    data = data
                )
            }

            NotificationType.REFUND_PROCESSED -> {
                NotificationTemplate(
                    title = "Remboursement traité",
                    body = "Votre remboursement a été effectué",
                    layoutResource = R.layout.notification_refund_processed,
                    expandedLayoutResource = R.layout.notification_refund_expanded,
                    data = data
                )
            }
        }
    }

    /**
     * Crée une vue personnalisée à partir du template et remplit les données
     */
    fun createCustomView(template: NotificationTemplate, isExpanded: Boolean = false): View {
        val layoutId = if (isExpanded && template.expandedLayoutResource != null) {
            template.expandedLayoutResource
        } else {
            template.layoutResource
        }

        val view = LayoutInflater.from(context).inflate(layoutId, null)
        populateView(view, template)
        return view
    }

    /**
     * Remplit la vue avec les données du template
     */
    private fun populateView(view: View, template: NotificationTemplate) {
        val data = template.data

        // Remplir les champs communs
        view.findViewById<TextView>(R.id.title_booking)?.text = template.title
        view.findViewById<TextView>(R.id.title_payment)?.text = template.title
        view.findViewById<TextView>(R.id.title_reminder)?.text = template.title
        view.findViewById<TextView>(R.id.title_booking_expanded)?.text = template.title
        view.findViewById<TextView>(R.id.title_payment_expanded)?.text = template.title

        // Remplir les données spécifiques selon le type
        when {
            // Réservation confirmée
            data.containsKey("destination") && data.containsKey("date") -> {
                val destination = data["destination"] as? String ?: "votre destination"
                val date = data["date"] as? String ?: "bientôt"

                view.findViewById<TextView>(R.id.message_booking)?.text =
                    "Votre voyage est confirmé"
                view.findViewById<TextView>(R.id.message_booking_expanded)?.text =
                    "Votre voyage vers $destination est confirmé pour le $date"
                view.findViewById<TextView>(R.id.destination_detail)?.text =
                    "Destination : $destination"
                view.findViewById<TextView>(R.id.date_detail)?.text =
                    "Date : $date"
            }

            // Paiement
            data.containsKey("amount") -> {
                val amount = data["amount"] as? String ?: "0€"

                view.findViewById<TextView>(R.id.message_payment)?.text =
                    "Paiement traité avec succès"
                view.findViewById<TextView>(R.id.message_payment_expanded)?.text =
                    "Votre paiement a été traité avec succès"
                view.findViewById<TextView>(R.id.amount_payment)?.text =
                    "Montant : $amount"
            }

            // Rappel de voyage
            data.containsKey("destination") -> {
                val destination = data["destination"] as? String ?: "votre destination"

                view.findViewById<TextView>(R.id.message_reminder)?.text =
                    "Votre voyage est demain !"
            }
        }
    }

    /**
     * Retourne la vue pour une notification simple (non étendue)
     */
    fun getCompactView(type: NotificationType, data: Map<String, Any>): View {
        val template = getTemplate(type, data)
        return createCustomView(template, false)
    }

    /**
     * Retourne la vue pour une notification étendue
     */
    fun getExpandedView(type: NotificationType, data: Map<String, Any>): View? {
        val template = getTemplate(type, data)
        return if (template.expandedLayoutResource != null) {
            createCustomView(template, true)
        } else {
            null
        }
    }
}