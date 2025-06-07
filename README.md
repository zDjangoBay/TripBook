TripBook - Application de Réservation de Vols-NotificationManager ✈️

Description
TripBook est une application Android moderne de réservation de vols avec système de notifications avancé, développée en Kotlin avec Jetpack Compose.

Quick Start

bash
setup_dev_environment.bat

dev_runner.ps1

hot_reload.bat


Fonctionnalités

Notifications Avancées
- Notifications temps réel (retards, annulations, changements de porte)
- Rappels automatiques check-in
- Actions rapides depuis notifications
- Priorité adaptative selon urgence

Réservations
- Flux multi-étapes : Transport → Hôtels → Activités → Paiement
- Suivi temps réel des réservations
- Recherche basée sur localisation
- Gestion : En attente, À venir, Terminées

Interface
- Material 3 Design (thème violet)
- Navigation bottom tabs
- Icônes de voyage (💼🧭📍🧘👥)
- Layouts responsives

Tech Stack

- Langage: Kotlin
- UI : Jetpack Compose + View Binding
- Architecture : MVVM + Repository Pattern
- Navigation : Navigation Compose
- Async : Coroutines
- Design : Material 3

Structure

app/src/main/java/com/android/tripbook/
├── data/models/                 # Modèles de données
├── notifications/               # Système notifications
│   ├── models/                 # InAppNotification, ServiceNotification
│   ├── services/               # NotificationService
│   └── receivers/              # NotificationReceiver, BootReceiver
├── ui/
│   ├── screens/                # Dashboard, Réservations, Notifications
│   ├── components/             # Composants réutilisables
│   └── theme/                  # Material 3 theme
└── MainActivity.kt


Configuration

Canaux Notifications
- CRITICAL : Annulations, embarquement fermé
- HIGH : Retards, changements de porte
- MEDIUM : Check-in, rappels
- LOW : Promotions, infos générales

Permissions
xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

Statut

Terminé
- Workflow réservation complet
- Système notifications multi-canaux
- Interface Material 3
- Environnement dev style Flutter

En cours
- Intégration API backend
- Authentification utilisateur
- Base de données Room
Tests

bash
 Tests unitaires
./gradlew test

 Tests UI
./gradlew connectedAndroidTest

 Test notifications
adb shell am broadcast -a com.tripbook.TEST_NOTIFICATION

 Équipe

- Notifications : Tchinda Martin Kevin
- Réservations : Équipe développement TripBook

 Installation

bash
git clone [URL_PROJET]
cd tripbook-reservation


Prêt en 3 étapes :
1. `setup_dev_environment.bat`
2. `dev_runner.ps1`
3. Start coding! 
