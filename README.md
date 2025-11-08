

# 🧠 MCQ Quiz App — Android (Jetpack Compose + MVVM)

A modern, delightful, and gamified MCQ quiz application built as part of the **SDE-II assignment for DailyRounds/Marrow**.
Designed with a strong emphasis on **UI/UX polish, architecture, and state reliability**.

---

## ✨ Features

### 🎮 Quiz Experience

* 10 question MCQ quiz parsed from JSON.
* Auto-advance after answer reveal (2s delay).
* Skip functionality.
* **Streak system** with reset on wrong answer.
* **Gamification elements** (streak badge, confetti, micro-animations).

### 🧠 Results & Analytics

* Score summary (Correct / Total).
* Longest streak tracked per session.
* Restart quiz with full state reset.

### 🎨 Premium UI/UX

* Fluid animations (fade, scale, transitions).
* Gradient themes and polished visual hierarchy.
* Interactive option selection feedback.
* Confetti celebration on streak achievements.
* Centered immersive layouts with motion elements.

### 🏗 Architecture & Code Quality

* **MVVM Architecture**
* Jetpack Compose UI
* State management using **StateFlow**
* Repository + ViewModel separation
* Clean and maintainable codebase
* Shared ViewModel via `activityViewModel`

---

## 🛠️ Tech Stack

| Component        | Technology                 |
| ---------------- | -------------------------- |
| UI               | Jetpack Compose            |
| Architecture     | MVVM                       |
| State Management | Kotlin StateFlow           |
| Networking       | Retrofit + Moshi           |
| Local Storage    | DataStore                  |
| Animations       | Lottie, Compose Animations |
| Navigation       | Compose Navigation         |

---

## 🧩 JSON Input Source

```
https://gist.githubusercontent.com/dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw
```

---

## 🧠 Core Logic Highlights

* ✅ Accurate scoring
* 🔁 Streak resets on wrong answers
* 🚫 Persistent longest streak across sessions is NOT used — only session streak shown
* ⏭ Auto-progress & skip handling
* 🧩 Defensive UI state handling (loading, error, empty, finished)

---

## 📸 UI Highlights

| Screen | Description                                  |
| ------ | -------------------------------------------- |
| Splash | Gradient intro with animation                |
| Quiz   | Animated options, progress bar, streak badge |
| Result | Score summary, longest streak & restart      |

---

## 🚀 Running the App

1. Clone the project

   ```sh
   git clone https://github.com/Gk15200-Aug/MCQApplication.git
   ```
2. Open in **Android Studio**
3. Sync Gradle & Run on device/emulator (API 24+)

---
