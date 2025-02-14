# Configuration Cache Problems Demonstration

This project demonstrates an issue with Gradle's configuration cache related to tasks that are not compatible with the configuration cache. Specifically, it highlights a scenario where tasks marked as incompatible are still being cached when `org.gradle.configuration-cache.problems=warn` is set in `gradle.properties`.

## Issue Description

Gradle's configuration cache is designed to improve build performance by caching the configuration phase of the build. However, some tasks are inherently incompatible with the configuration cache. When a task is marked as incompatible, it should be re-configured on each build, even if the configuration cache is enabled.

The problem arises when `org.gradle.configuration-cache.problems=warn` is set in `gradle.properties`. In this case, the `ConfigurationCacheProblems::shouldDiscardEntry` method incorrectly returns `false` for tasks that execute the `AbstractTask::notCompatibleWithConfigurationCache` function.

```kotlin
internal
class ConfigurationCacheProblems(/* ... */) {

    val shouldDiscardEntry: Boolean
        get() {
            /* some code */
            return discardStateDueToProblems(summary) || hasTooManyProblems(summary)
        }

    private
    fun discardStateDueToProblems(summary: Summary) =
        (summary.problemCount > 0 || incompatibleTasks.isNotEmpty()) && isFailOnProblems

}
```

This leads to the following unexpected behavior:

1.  **Incompatible tasks are cached:** Tasks that are explicitly marked as not compatible with the configuration cache are still being cached.
2.  **Stale task execution:** The task is stored on disk before it starts execution, causing Gradle to load the incompatible task from disk on every build as if it were a normal task that supports the configuration cache.
3.  **Incorrect re-configuration:** These tasks are not re-configured on subsequent builds, even though they should be.

This issue is related to the following Gradle issues:

*   [Gradle saves configuration cache for incompatible tasks](https://github.com/gradle/gradle/issues/32451)
*   [Explicit repository credentials break with configuration caching](https://github.com/gradle/gradle/issues/24040)

## Project Structure

The project is structured as follows:

*   `lib/`: Register the `FancyTask` which demonstrates the issue.
*   `gradle.properties`: Contains the configuration cache settings.

## Demonstration

The `lib` module apply [FancyPlugin](build-scripts/src/main/java/com/example/plugins/fancy/FancyPlugin.kt) that register a task called [FancyTask](build-scripts/src/main/java/com/example/plugins/fancy/tasks/FancyTask.kt). This task is configured as not compatible with the configuration cache using `notCompatibleWithConfigurationCache("Demonstrates configuration cache problem")`.

The `FancyTask::computeSpec` function generates a random number. The expected behavior is that this number should be different on each execution of the task.

## Steps to Reproduce

1.  Clone this repository.
2.  Navigate to the project root directory.
3.  Run the following command: `./gradlew :lib:fancy`
4.  Observe the output of the `FancyTask`. It will print a random number.
5.  Run the command again: `./gradlew :lib:fancy`
6.  **Observe the output.** You will notice that `FancyTask` prints the **same** value as the previous run. This is incorrect. The task should have generated a new random number.

## Expected Behavior

The `FancyTask` should print a different random number on each execution, even when `org.gradle.configuration-cache.problems=warn` is set. This is because the task is marked as not compatible with the configuration cache, and therefore should be re-configured every time.
