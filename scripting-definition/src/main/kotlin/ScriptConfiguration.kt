package reproducer.definition

import reproducer.definition.annotation.Import
import java.io.File
import kotlin.script.experimental.api.RefineScriptCompilationConfigurationHandler
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCollectedData
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptConfigurationRefinementContext
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptSourceAnnotation
import kotlin.script.experimental.api.asSuccess
import kotlin.script.experimental.api.collectedAnnotations
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.importScripts
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

internal class ReproScriptConfiguration : ScriptCompilationConfiguration(
    body = {
        defaultImports(Import::class)
        jvm {
            dependenciesFromClassContext(
                ReproScriptDefinition::class,
                "scripting-definition",
                "kotlin-stdlib",
            )
        }
        refineConfiguration {
            onAnnotations(Import::class, handler = ReproScriptImportsConfigurator())
        }
    }
)

internal class ReproScriptImportsConfigurator : RefineScriptCompilationConfigurationHandler {
    override fun invoke(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)
            ?.takeIf(List<ScriptSourceAnnotation<*>>::isNotEmpty)
        val importAnnotations = annotations?.filter { it.annotation is Import }?.takeIf(List<*>::isNotEmpty)
            ?: return context.compilationConfiguration.asSuccess()

        val workingDir = (context.script as? FileBasedScriptSource)?.file?.absoluteFile?.parentFile
        val diagnostics = arrayListOf<ScriptDiagnostic>()
        val scripts = importAnnotations.mapNotNull { (annotation, location) ->
            val path = (annotation as Import).path
            val file = if (workingDir != null) {
                File(workingDir, path).takeIf(File::isFile).also {
                    if (it == null) {
                        diagnostics += ScriptDiagnostic(
                            code = ScriptDiagnostic.unspecifiedError,
                            message = "cannot find import script file at ${workingDir.path}/$path",
                            severity = ScriptDiagnostic.Severity.ERROR,
                            locationWithId = location,
                        )
                    }
                }
            } else {
                diagnostics += ScriptDiagnostic(
                    code = ScriptDiagnostic.unspecifiedError,
                    message = "cannot find import script file with relative path ($path) from a script with unknown location",
                    severity = ScriptDiagnostic.Severity.ERROR,
                    locationWithId = location,
                )
                null
            }
            file?.toScriptSource()
        }

        return if (diagnostics.isNotEmpty()) {
            ResultWithDiagnostics.Failure(diagnostics)
        } else {
            ScriptCompilationConfiguration(context.compilationConfiguration) {
                if (scripts.isNotEmpty()) importScripts.append(scripts)
            }.asSuccess()
        }
    }
}
