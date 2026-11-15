/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import kotlin.test.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class ShellRootTest {
    // Keep initialization inside the timeout, rather than ShellTest's setup method: a su
    // implementation that treats a positional command as an interactive shell never returns.
    @Test(timeout = 10_000)
    fun initializeAndExecuteRootScript() {
        assumeTrue(Shell.isSessionRooted())
        assertEquals("0", Shell.executeScriptCaptureStdout("id -u").trim())
        assertEquals(
            Shell.Output("input with spaces\nquoted 'value'\n", "error output\n"),
            Shell.executeScriptCaptureStdoutStderr(
                """
                cat
                printf '%s\n' "quoted 'value'"
                printf '%s\n' 'error output' >&2
                """
                    .trimIndent(),
                stdin = "input with spaces\n",
            ),
        )
    }

    @Test(timeout = 10_000)
    @SdkSuppress(minSdkVersion = 31)
    fun initializeAndAccessShellFile() {
        assumeTrue(DeviceInfo.isRooted)
        val file = ShellFile.inTempDir("benchmark-root-shell-test.txt")
        try {
            file.writeText("shell file contents\n")
            assertEquals("shell file contents\n", file.readText())
        } finally {
            file.delete()
        }
    }
}
