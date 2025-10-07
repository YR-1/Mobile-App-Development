# NutriTrack — Mobile Nutrition Tracker (FIT2081 Assignment)

**Version:** 1.0

**Author:** `<Liew Yun Ru>`

## Short description

NutriTrack is a Kotlin + Jetpack Compose Android app that helps users track food quality scores, collect a short dietary questionnaire, and view personalized insights. The app seeds patient and score data from a provided CSV into a local Room database on first launch, implements MVVM architecture, supports multi-user login, and contains a NutriCoach section that integrates with the Fruityvice API and a GenAI service for personalised tips.

---

## Key features

* Welcome screen with disclaimer display. 
* Login claim flow:

  * Claim account on first login using `UserID` + `PhoneNumber`, set name & password.
  * Subsequent logins use `UserID` + password. Session persists until logout. 
* Questionnaire (stores responses locally in `FoodIntake` table).
* Home screen showing user greeting and **Food Quality Score** (loaded from DB). 
* Insights screen with category breakdown (progress bars) and total score.
* NutriCoach screen:

  * Fruits section: calls Fruityvice API when fruit score is not optimal.
  * GenAI section: generates short, personalised tips and saves them to `NutriCoachTips` table. 
* Settings & Admin:

  * Settings screen with logout and user info.
  * Admin view unlocked with passphrase `dollar-entry-apples` (shows aggregated stats + GenAI insights). 

---

## Project structure & architecture

* **MVVM**: `Repository` → `ViewModel` → `UI` (Jetpack Compose). Use LiveData to update UI reactively. 
* **Room DB**: `Patient`, `FoodIntake`, `NutriCoachTips` tables. CSV is used only once to seed Room on first run. 
* **Networking**: Retrofit + Kotlin Coroutines for async calls (Fruityvice + optional GenAI). 

---

## Prerequisites

* Android Studio
* JDK 11+
* Gradle (wrapped)
* Internet access for API calls (Fruityvice and GenAI if used)

---

## Installation & Run (developer quick-start)

1. Clone the repository:

   ```bash
   git clone <repo-url>
   cd NutriTrackPro
   ```
2. Open the project in **Android Studio**.
3. Put the CSV named `patients.csv` into `app/src/main/assets/`
4. Add API keys:

   * Add your API key to `local.properties` (or use secure keystore).
5. Build & run on an emulator or device.

---

## How data is handled

* On **first launch** the app reads the provided CSV and populates the Room database (`Patient` table etc.). After seeding, **the app will not read the CSV again**, all operations use Room via Repository and ViewModel. 
* Login validation checks `UserID` and `PhoneNumber` against the DB on account claim, later checks use `UserID` + password. 
