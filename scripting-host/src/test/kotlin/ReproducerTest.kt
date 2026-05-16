package reproducer.definition

import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.test.Test
import kotlin.test.assertIs

class ReproducerTest {

    private val scriptFile: File
        get() = File(Thread.currentThread().contextClassLoader.getResource("main.repro.kts")!!.toURI())

    // Passes
    @Test
    fun testWithLegacyHost() {
        val result = BasicJvmScriptingHost.createLegacy()
            .evalWithTemplate<ReproScriptDefinition>(scriptFile.toScriptSource())
        assertIs<ResultWithDiagnostics.Success<*>>(result)
    }

    // Fails
    @Test
    fun testWithNewHost() {
        val result = BasicJvmScriptingHost()
            .evalWithTemplate<ReproScriptDefinition>(scriptFile.toScriptSource())
        assertIs<ResultWithDiagnostics.Success<*>>(result)
    }
}
