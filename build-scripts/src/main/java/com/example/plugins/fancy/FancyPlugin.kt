@file:JvmName("FancyPlugin")

package com.example.plugins.fancy

import com.example.plugins.fancy.tasks.FancyTask
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class FancyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("fancy", FancyTask::class.java) {
            setStringValue("Fancy!")
            notCompatibleWithConfigurationCache("Just")
        }
    }
}
