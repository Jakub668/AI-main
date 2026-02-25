package com.example.ai

import android.content.Context

/**
 * Global registry for available features.  In a real app the list might be
 * loaded dynamically or via reflection; here it's hardcoded for demonstration.
 */
object FeatureRegistry {
    private val features = mutableListOf<Feature>()

    fun register(feature: Feature) {
        features.add(feature)
    }

    fun all(): List<Feature> = features

    fun activateAll(context: Context) {
        features.forEach { it.activate(context) }
    }
}

// register a core feature so there is at least one
init {
    FeatureRegistry.register(object : Feature {
        override val name: String = "Core chat"
        override fun activate(context: Context) {
            // no-op
        }
    })
}
