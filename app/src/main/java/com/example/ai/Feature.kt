package com.example.ai

import android.content.Context

/**
 * Simple interface for a "feature" module.  Each feature can register itself
 * in [FeatureRegistry] and modify the app when activated.
 *
 * This is purely structural; individual features (translation, voice,
 * calculator, etc.) would live in their own files or packages and implement
 * this interface.  The app ships with a placeholder "core" feature but you can
 * add as many as you like to compete with other AI assistants.
 */
interface Feature {
    /** human-readable name */
    val name: String
    /** Called when the feature is toggled on or when the app starts. */
    fun activate(context: Context)
    /** Optional cleanup when feature is disabled */
    fun deactivate(context: Context) {}
}
