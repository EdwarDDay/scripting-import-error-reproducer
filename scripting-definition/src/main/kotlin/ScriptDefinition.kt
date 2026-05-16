package reproducer.definition

import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "repro.kts",
    compilationConfiguration = ReproScriptConfiguration::class,
)
abstract class ReproScriptDefinition
