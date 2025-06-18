# 📱 TripBook - Application Android de Partage de Voyages Multilingue

[![Android](https://img.shields.io/badge/Android-API%2031+-brightgreen)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-blue)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.1-orange)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/ML%20Kit-Translation-red)](https://developers.google.com/ml-kit)

## 🌟 Vue d'ensemble

**TripBook** est une application Android moderne qui révolutionne le partage d'expériences de voyage en brisant les barrières linguistiques. Développée avec Jetpack Compose et ML Kit, elle offre une traduction automatique en temps réel et des fonctionnalités d'accessibilité avancées.

### ✨ Fonctionnalités Principales

🌐 **Traduction Automatique**
- Détection automatique de 11 langues
- Traduction instantanée hors ligne
- Support multilingue avec ML Kit

📸 **Partage de Photos Intelligent**
- Galeries adaptatives (1-5+ photos)
- Zoom plein écran avec navigation
- Chargement asynchrone optimisé

♿ **Accessibilité Complète**
- Synthèse vocale multilingue
- Mode malvoyants avec contrastes élevés
- Lecture audio des posts traduits

🎨 **Interface Moderne**
- Material Design 3
- Mode sombre/clair
- Animations fluides
- Tailles de texte personnalisables

## 🚀 Démonstration

### Exemple Concret : Post Manguier

```
👤 Marie claire                          🇪🇸 Espagnol

📸 [Photo du manguier avec enfant]

┌─ Texte original (espagnol) ─────────────────────┐
│ ¡Necesito este mango! Mi hijo está en el árbol │
│ y quiere cogerlo. ¡Qué aventura!               │
└─────────────────────────────────────────────────┘

┌─ Traduction française ──────────────────────────┐
│ J'ai besoin de cette mangue ! Mon fils est sur │
│ l'arbre et veut l'attraper. Quelle aventure !  │
└─────────────────────────────────────────────────┘

[🌐 Traduire] [🔊 Écouter]
```

## 🏗️ Architecture

### Structure du Projet

```
TripBook/
├── app/
│   ├── src/main/java/com/android/tripbook/
│   │   ├── posts/                   # Module Posts
│   │   │   ├── model/               # Modèles de données
│   │   │   ├── viewmodel/           # ViewModels MVVM
│   │   │   └── ui/components/       # Composants UI
│   │   ├── translation/             # Services ML Kit
│   │   ├── accessibility/           # Services TTS
│   │   ├── preferences/             # Gestion préférences
│   │   ├── settings/                # Écran paramètres
│   │   └── ui/theme/                # Thème Material 3
│   └── res/
├── doc/                             # Documentation
├── emulator_check.ps1              # Vérification émulateur
├── optimize_emulator.ps1           # Optimisation Android
└── README.md                       # Ce fichier
```

### Technologies Utilisées

| Composant | Technologie | Version |
|-----------|-------------|---------|
| 🔧 Langage | Kotlin | 2.0.20 |
| 🎨 UI Framework | Jetpack Compose | 1.5.1 |
| 🧠 IA/ML | Google ML Kit | 17.0.2+ |
| 🗣️ TTS | Android TextToSpeech | Built-in |
| 📱 Min SDK | Android 12 (API 31) | 31 |
| 🎯 Target SDK | Android 14 (API 34) | 34 |
| 🏗️ Build Tool | Gradle | 8.11.1 |

## 📱 Module Posts - Architecture Détaillée

### 📊 Modèles de Données

```kotlin
// Post.kt - Modèle principal
data class Post(
    val id: String,
    val content: String,
    val originalLanguage: String?,
    val translatedContent: String?,
    val author: String,
    val timestamp: Long,
    val photos: List<Uri> = emptyList()
)

// État de traduction
data class TranslationState(
    val isTranslating: Boolean = false,
    val error: String? = null
)
```

### 🎛️ ViewModel (MVVM)

**PostsViewModel** gère :
- 📝 État des posts avec StateFlow
- 🌐 Traductions automatiques
- 🔊 Synthèse vocale
- ⚙️ Préférences utilisateur

### 🎨 Composants UI

#### PhotoGallery.kt
- **Affichage adaptatif** selon le nombre de photos
- **Zoom plein écran** avec HorizontalPager
- **Chargement asynchrone** avec Coil

#### PostCard.kt
- **Interface Material 3** avec animations
- **Indicateurs de langue** détectée
- **Boutons contextuels** (Traduire, Écouter)
- **Mode accessibilité** avec contrastes élevés

## 🛠️ Installation et Configuration

### Prérequis

- **Android Studio** Hedgehog (2023.1.1+)
- **JDK 21** (Eclipse Adoptium recommandé)
- **Android SDK** API 31+
- **Émulateur Android** ou appareil physique

### 🚀 Démarrage Rapide

1. **Cloner le projet**
   ```bash
   git clone <repository-url>
   cd TripBook
   ```

2. **Configurer l'émulateur** (Windows)
   ```powershell
   .\emulator_check.ps1
   .\optimize_emulator.ps1
   ```

3. **Compiler et lancer**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### 📱 Émulateurs Recommandés

| Émulateur | RAM | CPU | GPU | API Level |
|-----------|-----|-----|-----|----------|
| 🏆 **Pixel_4** | 4096 MB | 4 cores | Host | 36 |
| ✅ **Pixel_6a** | 3072 MB | 4 cores | Host | 34 |
| ⚡ **Pixel_7** | 4096 MB | 6 cores | Host | 35 |

## 🔧 Fonctionnalités Avancées

### 🌍 Langues Supportées

| Code | Langue | Détection | Traduction | TTS |
|------|--------|-----------|------------|-----------|
| `fr` | Français | ✅ | ✅ | ✅ |
| `en` | Anglais | ✅ | ✅ | ✅ |
| `es` | Espagnol | ✅ | ✅ | ✅ |
| `de` | Allemand | ✅ | ✅ | ✅ |
| `it` | Italien | ✅ | ✅ | ✅ |
| `pt` | Portugais | ✅ | ✅ | ✅ |
| `ar` | Arabe | ✅ | ✅ | ✅ |
| `zh` | Chinois | ✅ | ✅ | ✅ |
| `ja` | Japonais | ✅ | ✅ | ✅ |
| `ko` | Coréen | ✅ | ✅ | ✅ |
| `ru` | Russe | ✅ | ✅ | ✅ |

### ♿ Accessibilité

- **TalkBack** natif Android
- **Synthèse vocale** multilingue
- **Contrastes élevés** en mode accessibilité
- **Tailles de texte** personnalisables
- **Navigation clavier** complète

## 🧪 Tests et Qualité

### Structure de Tests

```
app/src/
├── test/                    # Tests unitaires
├── androidTest/             # Tests d'intégration
└── main/
```

### Commandes de Test

```bash
# Tests unitaires
./gradlew test

# Tests d'intégration
./gradlew connectedAndroidTest

# Analyse de code
./gradlew lint
```

## 📚 Documentation

- 📖 [Guide de Développement](doc/DEVELOPMENT.md)
- 🎯 [Guide de Contribution](doc/CONTRIBUTING.md)
- 🚀 [Démo Manguier](demo_mango_test.md)
- 📋 [Résumé Fonctionnalités](DEMO_MANGO_RESUME.md)

## 🤝 Contribution

### Branches

- `main` - Branche principale stable
- `Posts-Zambo-Joseph-Antoine-ICTU20223268` - Module Posts
- `feature/*` - Nouvelles fonctionnalités
- `bugfix/*` - Corrections de bugs

### Workflow Git

```bash
# Créer une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# Commits atomiques
git add .
git commit -m "feat(posts): ajouter support photos multiples"

# Push et Pull Request
git push origin feature/nouvelle-fonctionnalite
```

## 📊 Métriques et Performance

### Optimisations

- **Chargement images** : Coil avec cache mémoire/disque
- **Traduction ML** : Modèles hors ligne (pas d'internet requis)
- **StateFlow** : Gestion d'état réactive efficace
- **Composition** : Recomposition minimale avec remember

### Taille APK

- **Debug** : ~15 MB
- **Release** : ~8 MB (avec ProGuard)
- **ML Models** : ~12 MB (inclus dans l'APK)

## 🔐 Sécurité et Confidentialité

- **Traduction locale** : Aucune donnée envoyée vers des serveurs
- **Photos locales** : Stockage sécurisé sur l'appareil
- **Permissions minimales** : Caméra et stockage uniquement

## 🗺️ Roadmap

### Version 1.1 (Q2 2025)
- [ ] OCR sur photos (extraction texte des images)
- [ ] Géolocalisation automatique
- [ ] Export PDF des posts

### Version 1.2 (Q3 2025)
- [ ] Synchronisation cloud
- [ ] Partage social
- [ ] Mode collaboratif

### Version 2.0 (Q4 2025)
- [ ] IA génératrice de légendes
- [ ] Réalité augmentée
- [ ] Support tablettes

## 📞 Support

- 💬 **Issues** : [GitHub Issues](https://github.com/repo/issues)
- 📧 **Email** : support@tripbook.com
- 📖 **Documentation** : [Wiki du projet](https://github.com/repo/wiki)

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENCE](LICENCE) pour plus de détails.

---

⭐ **N'hésitez pas à mettre une étoile si ce projet vous plaît !** ⭐

*Développé avec ❤️ pour briser les barrières linguistiques en voyage*

# TripBook
TripBook: A mobile social network for travelers exploring Africa &amp; beyond. Share stories, photos, and tips, rate travel agencies, and connect with adventurers. Community-driven platform to discover hidden gems, promote tourism, and ensure safer journeys. Built with React Native, Node.js &amp; geolocation APIs. Contributions welcome! 🌍✨