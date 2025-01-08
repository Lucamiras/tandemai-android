# Tandem: AI-Powered Language Learning Partner

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-v1.9.10-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.1-green.svg)](https://developer.android.com/jetpack/compose)

## Overview

Tandem AI is an Android implementation of a Streamlit app I built in 2024. It is designed to help language learners practice conversational language where simple vocabulary and grammar apps are not enough.
I love learning languages, and I do believe that a tandem partner is a great way to practice the language properly. However, it tends to be difficult to find a person in your time zone that matches with you and your language needs, especially for lesser known, smaller languages.

![Alt Text](Images/tandemai.gif)

## Features

*   **AI-Powered Conversations:** Chat with an AI that understands your target language and skill level.
*   **Real-time Mistake Correction:**  Get instant feedback on grammatical errors, vocabulary usage, and style.
*   **Customizable Difficulty:** Choose your proficiency level (Beginner, Intermediate, Advanced) to tailor the conversation complexity.
*   **Multi-Language Support:** Learn a variety of languages (e.g., English, Spanish, French, German, and more).
*   **Multiple Scenarios available:** Role-play with your tandem partner and practice for job interviews and more.

## Technical Overview

*   **Architecture:** Model-View-ViewModel (MVVM) for a clean separation of concerns and improved testability.
*   **Language:** 100% Kotlin, leveraging coroutines and Flows for efficient asynchronous operations.
*   **UI Framework:** Jetpack Compose for building a dynamic and responsive user interface.
*   **LLM Integration:** Abstracted interface (`LLMRepository`) to interact with different LLM APIs or local models, providing flexibility for future expansion.
*   **State Management:** `StateFlow` and `MutableStateFlow` to manage and react to UI state changes efficiently.
*   **Navigation:** Jetpack Compose Navigation for seamless transitions between screens.

## Getting Started

### Prerequisites

*   Android Studio Hedgehog | 2023.1.1 or later
*   Android SDK 34 or later
*   Kotlin 1.9.10 or later
*   Gemini API Key (if using a remote LLM API) - Support for other LLMs and local models coming soon 

### Installation

1.  Clone the repository:

    ```bash
    git clone <repository-url>
    ```

2.  Open the project in Android Studio.

3.  (If applicable) Add your LLM API key to the `local.properties` file:

    ```
    GEMINI_API_KEY=your_api_key_here
    ```

4.  Build and run the app on an emulator or physical device.

## Project Structure

```
com.lucamiras.tandemai
├── ui              // UI-related code (Composables and ViewModels)
│   ├── featureSetup
│   │   ├── SetupScreen.kt      // Composable for initial setup
│   │   └── SetupViewModel.kt   // ViewModel for setup logic
│   ├── featureChat
│   │   ├── ChatScreen.kt       // Composable for the chat interface
│   │   └── ChatViewModel.kt    // ViewModel for chat interactions
│   ├── featureMistakes
│   │   ├── MistakesScreen.kt  // Composable for displaying mistakes
│   │   └── MistakesViewModel.kt // ViewModel for managing mistakes
├── data             // Data models, repositories, and data holders
│   ├── model
│   │   ├── Language.kt         // Enum representing supported languages
│   │   ├── SkillLevel.kt       // Enum representing skill levels
│   │   ├── Scenario.kt         // Enum representing scenarios
│   │   ├── Message.kt          // Data class for chat messages
│   │   └── Mistake.kt          // Data class for representing mistakes
│   └── repository
│       ├── LLMRepository.kt    // Interface for LLM interaction
│       └── LLMRepositoryImpl.kt // Implementation of LLMRepository
│   
├── navigation     // Navigation graph (optional)
│   └── AppNavigation.kt        // Composable for navigation
└── MainActivity.kt  // Main activity
```