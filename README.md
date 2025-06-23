# TripBook - Application de Réservation de Vols ✈️

 Description
TripBook est une application Android moderne de réservation de vols développée en Kotlin. L'application offre un système de notifications avancé pour tenir les utilisateurs informés de tous les aspects de leur voyage.

  Fonctionnalités Principales

 Système de Notifications Avancé
- Notifications en temps réel pour les vols (retards, annulations, changements de porte)
- Rappels automatiques pour le check-in et les documents de voyage
- Notifications push et notifications in-app
- Actions rapides directement depuis les notifications
- Priorité adaptative selon l'urgence de l'information

 Types de Notifications Supportées
- ✅ Confirmation et modification de réservation
- ✅ Statut des vols (retards, annulations, reprogrammation)
- ✅ Changements d'embarquement (porte, terminal)
- ✅ Rappels de check-in et attribution de sièges
- ✅ Informations météo et conseils de voyage
- ✅ Offres de surclassement et services additionnels

Stack Technique

Technologies Utilisées
- Langage : Kotlin
- Architecture : MVVM avec LiveData
- UI : View Binding + Material Design
- Asynchrone : Coroutines Kotlin
- Navigation : Android Navigation Component

 Dépendances Principales
gradle
// Core Android
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0

// Architecture Components
androidx.lifecycle:lifecycle-livedata-ktx:2.7.0
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3

// Navigation
androidx.navigation:navigation-fragment-ktx:2.7.6


##  Configuration Minimale
- API Level minimum : 24 (Android 7.0)
- API Level cible : 34 (Android 14)
- Version de compilation : 34

##  Architecture du Projet

```
com.tripbook.reservation/
├── notifications/
│   ├── models/
│   │   ├── InAppNotification.kt        # Modèle de notification in-app
│   │   ├── ServiceNotification.kt      # Modèle de communication service
│   │   └── NotificationEnums.kt        # Types et priorités
│   ├── bridge/
│   │   └── NotificationBridge.kt       # Interface de communication
│   ├── services/
│   │   └── NotificationService.kt      # Service de notifications
│   └── receivers/
│       ├── NotificationReceiver.kt     # Gestionnaire d'actions
│       └── BootReceiver.kt            # Redémarrage automatique
├── ui/
│   ├── activities/
│   ├── fragments/
│   └── viewmodels/
└── utils/
```

##  Installation et Configuration

### Prérequis
- Android Studio Arctic Fox ou plus récent
- JDK 8 ou supérieur
- SDK Android 34

### Étapes d'installation
1. Cloner le projet
   ```bash
   git clone [URL_DU_PROJET]
   cd tripbook-reservation
   ```

2. Ouvrir dans Android Studio
    - File → Open → Sélectionner le dossier du projet

3. Synchroniser les dépendances
    - Android Studio synchronisera automatiquement les dépendances Gradle

4. Configurer les permissions
    - Les permissions sont déjà configurées dans le AndroidManifest.xml
    - Pour Android 13+, demander la permission POST_NOTIFICATIONS à l'utilisateur

Configuration des Notifications

Canaux de Notifications
L'application utilise plusieurs canaux de notifications :
- CRITICAL : Vols annulés, embarquement fermé
- HIGH : Retards, changements de porte
- MEDIUM : Check-in, rappels
- LOW : Promotions, informations générales
- INFO : Météo, points de fidélité

 Actions Disponibles
- Voir la réservation
- S'enregistrer maintenant
- Voir le statut du vol
- Contacter le support
- Choisir un siège
- Et bien d'autres...

  Tests

### Tests Unitaires
```bash
./gradlew test
```

### Tests d'Interface
```bash
./gradlew connectedAndroidTest
```

##  Permissions Requises

### Permissions Essentielles
- `INTERNET` : Communication avec les serveurs
- `ACCESS_NETWORK_STATE` : Vérification de la connectivité
- `POST_NOTIFICATIONS` : Affichage des notifications (Android 13+)
- `WAKE_LOCK` : Maintien de l'activité pour les notifications critiques
- `VIBRATE` : Notifications avec vibration

### Permissions Optionnelles
- `RECEIVE_BOOT_COMPLETED` : Redémarrage automatique du service
- `FOREGROUND_SERVICE` : Service de notifications en arrière-plan

##  Configuration Personnalisée

### Personnaliser les Notifications
1. Modifier les types dans `NotificationEnums.kt`
2. Adapter les modèles dans `InAppNotification.kt`
3. Configurer les actions dans `NotificationAction`

### Ajouter de Nouveaux Types de Vols
1. Étendre l'enum `NotificationType`
2. Ajouter les champs nécessaires dans `ServiceNotification`
3. Implémenter la logique dans le service

##  Développeur
**Responsable** : Tchinda Martin Kevin

##  Licence
Ce projet est développé pour TripBook. Tous droits réservés.

##  Support
Pour toute question ou problème :
- Créer une issue dans le repository
- Contacter l'équipe de développement TripBook

---
Application développée avec amour pour améliorer l'expérience de voyage des utilisateurs