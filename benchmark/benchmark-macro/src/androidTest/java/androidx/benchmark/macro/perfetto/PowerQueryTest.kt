/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.benchmark.macro.perfetto

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.PowerCategory
import androidx.benchmark.macro.PowerMetric.Companion.MEASURE_BLOCK_SECTION_NAME
import androidx.benchmark.macro.createTempFileFromAsset
import androidx.benchmark.perfetto.PerfettoHelper.Companion.isAbiSupported
import androidx.benchmark.runSingleSessionServer
import androidx.benchmark.traceprocessor.TraceProcessor
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import kotlin.test.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMetricApi::class)
@SdkSuppress(minSdkVersion = 29)
@RunWith(AndroidJUnit4::class)
@SmallTest
class PowerQueryTest {
    @Test
    fun successfulFixedTrace() {
        assumeTrue(isAbiSupported())

        val traceFile = createTempFileFromAsset("api32_odpm_rails", ".perfetto-trace")
        val actualMetrics =
            TraceProcessor.runSingleSessionServer(traceFile.absolutePath) {
                PowerQuery.getPowerMetrics(
                    this,
                    querySlices(MEASURE_BLOCK_SECTION_NAME, packageName = null).first(),
                )
            }

        assertPowerMeasurements(
            mapOf(
                PowerCategory.CPU to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 390378.0,
                        powerUw = 80940.90814845532000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "CpuBig",
                                    energyUws = 31935.0,
                                    powerUw = 6621.397470454074000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "CpuLittle",
                                    energyUws = 303264.0,
                                    powerUw = 62878.706199460915000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "CpuMid",
                                    energyUws = 55179.0,
                                    powerUw = 11440.804478540327000,
                                ),
                            ),
                    ),
                PowerCategory.DISPLAY to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 1006934.0,
                        powerUw = 208777.52436243003000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "Display",
                                    energyUws = 1006934.0,
                                    powerUw = 208777.52436243003000,
                                )
                            ),
                    ),
                PowerCategory.GPU to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 66555.0,
                        powerUw = 13799.502384408045000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "Gpu",
                                    energyUws = 66555.0,
                                    powerUw = 13799.502384408045000,
                                )
                            ),
                    ),
                PowerCategory.MEMORY to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 355440.0,
                        powerUw = 73696.86916856728000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "DdrA",
                                    energyUws = 48458.0,
                                    powerUw = 10047.273481235745000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "DdrB",
                                    energyUws = 54988.0,
                                    powerUw = 11401.202571013892000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "DdrC",
                                    energyUws = 100082.0,
                                    powerUw = 20750.98486419241000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "MemoryInterface",
                                    energyUws = 151912.0,
                                    powerUw = 31497.408252125233000,
                                ),
                            ),
                    ),
                PowerCategory.MACHINE_LEARNING to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 50775.0,
                        powerUw = 10527.679867302508000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "Tpu",
                                    energyUws = 50775.0,
                                    powerUw = 10527.679867302508000,
                                )
                            ),
                    ),
                PowerCategory.NETWORK to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 596810.0,
                        powerUw = 123742.48393116318000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "AocLogic",
                                    energyUws = 74972.0,
                                    powerUw = 15544.681733360978000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "AocMemory",
                                    energyUws = 19601.0,
                                    powerUw = 4064.0680074642335000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "Modem",
                                    energyUws = 8369.0,
                                    powerUw = 1735.2270371138296000,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "RadioFrontend",
                                    energyUws = 0.0,
                                    powerUw = 0.0,
                                ),
                                PowerQuery.ComponentMeasurement(
                                    name = "WifiBt",
                                    energyUws = 493868.0,
                                    powerUw = 102398.50715322413000,
                                ),
                            ),
                    ),
                PowerCategory.UNCATEGORIZED to
                    PowerQuery.CategoryMeasurement(
                        energyUws = 122766.0,
                        powerUw = 25454.281567489115000,
                        components =
                            listOf(
                                PowerQuery.ComponentMeasurement(
                                    name = "SystemFabric",
                                    energyUws = 122766.0,
                                    powerUw = 25454.281567489115000,
                                )
                            ),
                    ),
            ),
            actualMetrics,
        )
    }

    @Test
    fun powerUsesSecondsForNanosecondSamples() {
        assumeTrue(isAbiSupported())
        val traceFile = createTempFileFromAsset("api32_odpm_rails", ".perfetto-trace")
        TraceProcessor.runSingleSessionServer(traceFile.absolutePath) {
            val window = querySlices(MEASURE_BLOCK_SECTION_NAME, packageName = null).first()
            val samples =
                query(
                        "SELECT c.ts, c.value FROM counter c JOIN counter_track t ON c.track_id = t.id WHERE t.name = 'power.rails.gpu' ORDER BY c.ts"
                    )
                    .filter { it.long("ts") in window.ts..window.endTs }
                    .toList()
            val first = samples.first()
            val last = samples.last()
            val energyMicrojoules = last.double("value") - first.double("value")
            val elapsedNanoseconds = last.long("ts") - first.long("ts")
            // One watt is one joule per second; trace timestamps are nanoseconds.
            val expectedMicrowatts = energyMicrojoules * 1_000_000_000.0 / elapsedNanoseconds
            val measured = PowerQuery.getPowerMetrics(this, window).getValue(PowerCategory.GPU)
            assertEquals(expectedMicrowatts, measured.powerUw, absoluteTolerance = 0.00001)
        }
    }

    private fun assertPowerMeasurements(
        expected: Map<PowerCategory, PowerQuery.CategoryMeasurement>,
        actual: Map<PowerCategory, PowerQuery.CategoryMeasurement>,
    ) {
        assertEquals(expected.keys, actual.keys)
        for ((category, wanted) in expected) {
            val observed = actual.getValue(category)
            assertEquals(wanted.energyUws, observed.energyUws)
            // Match PowerMetricTest's tolerance for rounding in floating-point category sums.
            assertEquals(wanted.powerUw, observed.powerUw, absoluteTolerance = 0.00001)
            assertEquals(wanted.components.map { it.name }, observed.components.map { it.name })
            wanted.components.zip(observed.components).forEach {
                (wantedComponent, observedComponent) ->
                assertEquals(wantedComponent.energyUws, observedComponent.energyUws)
                assertEquals(
                    wantedComponent.powerUw,
                    observedComponent.powerUw,
                    absoluteTolerance = 0.00001,
                )
            }
        }
    }

    @Test
    fun emptyFixedTrace() {
        assumeTrue(isAbiSupported())

        val traceFile = createTempFileFromAsset("api31_odpm_rails_empty", ".perfetto-trace")

        val actualMetrics =
            TraceProcessor.runSingleSessionServer(traceFile.absolutePath) {
                PowerQuery.getPowerMetrics(
                    this,
                    querySlices(MEASURE_BLOCK_SECTION_NAME, packageName = null).first(),
                )
            }

        assertEquals(emptyMap(), actualMetrics)
    }
}
