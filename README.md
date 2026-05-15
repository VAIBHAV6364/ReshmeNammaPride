<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://github.com/user-attachments/assets/0aa67016-6eaf-458a-adb2-6e31a0763ed6" />
</div>

# Reshme-Namma Pride (ರೆಷ್ಮೆ-ನಮ್ಮ ಪ್ರೈಡ್)
### *Smart Sericulture Rearing Management System*

**Reshme-Namma Pride** is a specialized Android application designed to empower Indian silk farmers with scientific monitoring and batch management tools. By bridging the gap between traditional rearing practices and micro-climatic requirements, the app helps farmers maximize their cocoon yield and silk quality.

---

## 📖 Table of Contents
1. [Project Vision](#-project-vision)
2. [Problem & Solution](#-problem--solution)
3. [Core Features](#-core-features)
4. [Technical Implementation](#-technical-implementation)
5. [Database Schema](#-database-schema)
6. [Tech Stack](#-tech-stack)
7. [App Navigation Guide](#-app-navigation-guide)
8. [Future Roadmap](#-future-roadmap)

---

## 🎯 Project Vision
Sericulture is a precise biological science. **Reshme-Namma Pride** aims to provide farmers with a "Digital Supervisor" that works entirely offline, tracking the delicate life cycle of silkworms across multiple batches simultaneously to ensure no crop is lost due to environmental imbalances.

---

## ⚠️ Problem & Solution
*   **The Problem**: Silkworms are extremely sensitive. A 2°C temperature fluctuation or a 10% humidity drop during early instars can lead to high mortality or poor quality "non-breakable" silk. Managing multiple batches of different varieties (e.g., Kolar Gold vs. Pure Mysore) at different stages is mentally taxing for farmers.
*   **The Solution**: An intelligent advisory system that maps current room conditions against the specific biological needs of the silkworm’s current "Instar" stage and variety, providing instant corrective actions.

---

## ✨ Core Features
*   **Multi-Batch Management**: Create and track multiple rearing cycles with custom names (e.g., "Lot A - North Room").
*   **Variety-Specific Tracking**: Support for common Indian varieties like Pure Mysore (PM), CSR2 x CSR4, Kolar Gold, M5, V1, and more.
*   **Instar Lifecycle Logic**: Start tracking from any stage (1st Instar to Cocooning). The app automatically calculates the "Age" and biological stage of the batch.
*   **Real-time Environmental Status**: 
    *   🟢 **Optimal**: Conditions match scientific requirements.
    *   🟠 **Warning**: Minor deviation detected.
    *   🔴 **Critical**: Immediate action required to save the crop.
*   **Expert Advisory Engine**: Detailed corrective instructions (e.g., "Apply lime powder," "Spread wet gunny bags," "Improve ventilation").
*   **Instar Knowledge Base**: A digital handbook covering stage descriptions, ideal ranges, and a list of "Do’s and Don’ts" for each phase.
*   **Device Responsive**: Optimized UI that scales from small smartphones to large tablets.

---

## 🛠 Technical Implementation
The app follows a modern **Clean Architecture (MVVM)** pattern:

### 🧠 The InstarEngine
The "Brain" of the app. It contains a comprehensive requirement matrix for all stages.
*   **Dynamic Logic**: It compares `CurrentTemp` and `CurrentHum` against the requirements of the specific `InstarStage` to generate the `StatusColor` and `AdviceList`.

### 💾 Offline-First Architecture
Using **Room Persistence Library**, the app stores all data locally. Farmers do not need an internet connection to record logs or view advice, which is critical for rural accessibility.

---

## 🗄 Database Schema
The database (Version 4) utilizes two primary relational tables:

### 1. `batches` Entity

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | String (PK) | Unique UUID |
| `name` | String | User-defined Batch Name |
| `variety` | String | Silkworm variety type |
| `startStage` | Int | Starting instar (1-6) |
| `startDate` | Long | Timestamp of batch start |
| `isActive` | Boolean | Rearing status |

### 2. `climate_logs` Entity
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Int (PK) | Auto-incrementing ID |
| `batchId` | String (FK) | Link to parent batch |
| `temperature` | Float | Recorded Temp (°C) |
| `humidity` | Float | Recorded Humidity (%) |
| `timestamp` | Long | Log entry time |

---

## 🚀 Tech Stack
*   **Language**: Kotlin
*   **UI**: Jetpack Compose (Declarative UI)
*   **Design**: Material Design 3 (M3)
*   **Database**: Room DB (SQLite Abstraction)
*   **Concurrency**: Kotlin Coroutines & Flow (Real-time data streaming)
*   **Navigation**: Navigation Compose
*   **Responsiveness**: Compose WindowSizeClass

---

## 📱 App Navigation Guide
1.  **Dashboard (Batches)**: View cards of all active batches. Each card shows the current stage, last recorded temp/humidity, and a color-coded status indicator.
2.  **Detail View**: Tap any batch to see its full history, record new readings via sliders, and read stage-specific advice.
3.  **New Batch**: Configure a new rearing cycle by naming it and selecting the variety/starting stage.
4.  **Info Tab**: Read the "Knowledge Base" for best practices on every instar stage.

---

## 🗺 Future Roadmap
*   **Bluetooth Integration**: Automatic logging via low-cost DHT22 sensors.
*   **Localization**: Full support for Kannada and Telugu languages.
*   **Market Intelligence**: Live price tracking of cocoon auctions in Mandis (Kolar, Ramanagara, etc.).
*   **AI Diagnostics**: Image-based detection of silkworm diseases (Grasserie, Flacherie).

---
**Developed for the betterment of Indian Silk Farmers.** 🇮🇳🌱
