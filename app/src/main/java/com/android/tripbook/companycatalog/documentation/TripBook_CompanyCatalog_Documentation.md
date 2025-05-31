# TripBook Android App - Company Catalog Module

This document provides an overview of the TripBook Android application's **Company Catalog** module, built using Jetpack Compose and organized according to the MVC architecture pattern. The module allows users to browse a catalog of companies, view detailed profiles, and filter/search through listings using mock data.

---

## 1. Product Backlog

| ID  | User Story                                                              | Priority | Status    | Notes                                                |
| --- | ----------------------------------------------------------------------- | -------- | --------- | ---------------------------------------------------- |
| US1 | As a user, I want to see a list of companies so that I can browse them. | High     | Completed | Implemented in `CompanyCatalogScreen.kt`.            |
| US2 | As a user, I want to switch between grid and list views for companies.  | Medium   | Completed | View toggle in `ViewModeToggleButtons.kt`.           |
| US3 | As a user, I want to view more details about a company when I click it. | High     | Completed | Navigation handled by `AppNavigation.kt`.            |
| US4 | As a user, I want to search companies by name.                          | High     | Completed | Implemented in `SearchBar.kt`.                       |
| US5 | As a user, I want to see a company's services and contact details.      | Medium   | Completed | Provided in `CompanyServices.kt` and `ContactUs.kt`. |
| US6 | As a user, I want to see a friendly message if no companies are found.  | Low      | Completed | Implemented in `EmptyState.kt`.                      |

> 💡 **Note:** This project does not use a real backend or database. `MockCompanyData.kt` simulates a database with sample companies.
---

## 2. Architecture Overview

The module follows the **Model-View-Controller (MVC)** pattern.

- **Model**: Contains data classes and mock repository (`Company`, `CompanyContact`, `CompanyService`, `CompanyRepository`, `MockCompanyData`).
- **View**: Composables organized by screen (`catalog`, `detail`) and reusable `components`.
- **Controller**: Handles app logic (`CompanyController.kt`) and navigation (`NavigationController.kt`).

---

## 3. Navigation Flow

- **Start** at `CompanyCatalogActivity.kt`, which hosts the navigation system.
- From the **Company Catalog Screen**, users can:
  - View companies in a list or grid format.
  - Use the search bar to filter companies.
  - Tap a company card to navigate to the **Detail Screen**.
- In the **Company Detail Screen**, users can:
  - Read company descriptions.
  - View services (`CompanyServices.kt`).
  - Access contact info (`ContactUs.kt`).

Navigation is managed via `AppNavigation.kt` and `Routes.kt` using Jetpack Compose’s Navigation library.

---

## 4. Project Structure

The `companycatalog` module is organized to promote maintainability and modularity:

```
companycatalog/
│
├── CompanyCatalogActivity.kt             // Entry activity hosting navigation
│
├── controller/
│   ├── CompanyController.kt              // Business logic and app state
│   └── NavigationController.kt           // Routing logic
│
├── model/
│   ├── Company.kt                        // Main data model
│   ├── CompanyContact.kt                 // Contact information model
│   ├── CompanyRepository.kt              // Repository abstraction
│   ├── CompanyService.kt                 // Company services model
│   └── MockCompanyData.kt                // Mock data simulating a backend
│
├── navigation/
│   ├── AppNavigation.kt                  // Navigation setup with NavHost
│   └── Routes.kt                         // Constants for navigation routes
│
├── ui/
│   ├── catalog/                          // Main catalog view and search UI
│   │   ├── CompanyCard.kt
│   │   ├── CompanyCatalogScreen.kt
│   │   ├── CompanyListCard.kt
│   │   └── SearchBar.kt
│   │
│   ├── components/                       // Reusable UI components
│   │   ├── EmptyState.kt
│   │   ├── ImageLoader.kt
│   │   ├── SectionTitle.kt
│   │   ├── TopBar.kt
│   │   └── ViewModeToggleButtons.kt
│   │
│   └── detail/                           // Company detail view
│       ├── CompanyDescription.kt
│       ├── CompanyDetailScreen.kt
│       ├── CompanyServices.kt
│       └── ContactUs.kt
```

> 📁 **Scalable Design**: This structure allows easy addition of features, UI states, or screens in the future without cluttering.
---

## 5. Future Adjustments

The incorporation of a database that stores company catalog information, is what we will focus on after this demo 
