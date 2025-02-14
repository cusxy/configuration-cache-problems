package com.example.plugins.fancy.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.serialization.Cached
import org.gradle.internal.serialization.Transient
import org.gradle.internal.serialization.Transient.varOf
import kotlin.random.Random
import kotlin.random.nextUInt

abstract class FancyTask : DefaultTask() {

    private val logger = Logging.getLogger(this::class.java)

    private val stringValue: Transient.Var<String> = varOf()
    private val spec: Cached<FancySpec> = Cached.of(this::computeSpec)

    fun setStringValue(stringValue: String) {
        this.stringValue.set(stringValue)
    }

    @TaskAction
    fun fancy() {
        val spec = this.spec.get()
        logger.quiet(spec.stringValue)
        logger.quiet("isCompatibleWithConfigurationCache=${isCompatibleWithConfigurationCache}")
    }

    private fun computeSpec(): FancySpec {
        val stringValue = this.stringValue.get()
        checkNotNull(stringValue) { "The 'stringValue' is required" }

        return FancySpec(
            stringValue = "$stringValue: ${Random.nextUInt()}"
        )
    }

    data class FancySpec(
        val stringValue: String,
    )
}
