package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.di

import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.provider.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.repository.TransactionRepository
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.payement.service.PaymentService
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.util.ValidationUtils

/**
 * Module de paiement qui gère les dépendances de la fonctionnalité de paiement
 * Utilisation du pattern Singleton avec initialisation paresseuse (lazy)
 * au lieu de l'injection de dépendances avec Dagger/Hilt
 */
object PaymentModule {

    /**
     * Instance unique de ValidationUtils
     */
    val validationUtils: ValidationUtils by lazy {
        ValidationUtils()
    }

    /**
     * Repository pour la gestion des transactions
     */
    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository()
    }

    /**
     * Fournisseur de service de paiement
     */
    val paymentProvider: PaymentProvider by lazy {
        MockPaymentProvider() // Pour le développement
        // Pourrait être remplacé par une implémentation réelle en production
    }

    /**
     * Service de paiement avec toutes les dépendances injectées
     */
    val paymentService: PaymentService by lazy {
        PaymentService(paymentProvider, transactionRepository, validationUtils)
    }
}