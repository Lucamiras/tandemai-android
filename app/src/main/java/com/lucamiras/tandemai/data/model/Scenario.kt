package com.lucamiras.tandemai.data.model

enum class Scenario(val short: String, val description: String) {
    NONE("No specific scenario", ""),
    JOB("Job Interview", "You are an applicant for a job. The interviewer will ask you typical questions."),
    RESTAURANT("Restaurant", "You are a guest at a restaurant. You can ask for the menu, order food, etc.");
}