// port-lint: source lib.rs
package io.github.kotlinmania.cpal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class LibTest {
    @Test
    fun testCmpDefaultHeuristics() {
        val formats = mutableListOf(
            SupportedStreamConfigRange(
                bufferSize = SupportedBufferSize.Range(min = 256u, max = 512u),
                channels = 2u,
                minSampleRate = SampleRate(1u),
                maxSampleRate = SampleRate(96_000u),
                sampleFormat = SampleFormat.F32,
            ),
            SupportedStreamConfigRange(
                bufferSize = SupportedBufferSize.Range(min = 256u, max = 512u),
                channels = 1u,
                minSampleRate = SampleRate(1u),
                maxSampleRate = SampleRate(96_000u),
                sampleFormat = SampleFormat.F32,
            ),
            SupportedStreamConfigRange(
                bufferSize = SupportedBufferSize.Range(min = 256u, max = 512u),
                channels = 2u,
                minSampleRate = SampleRate(1u),
                maxSampleRate = SampleRate(96_000u),
                sampleFormat = SampleFormat.I16,
            ),
            SupportedStreamConfigRange(
                bufferSize = SupportedBufferSize.Range(min = 256u, max = 512u),
                channels = 2u,
                minSampleRate = SampleRate(1u),
                maxSampleRate = SampleRate(96_000u),
                sampleFormat = SampleFormat.U16,
            ),
            SupportedStreamConfigRange(
                bufferSize = SupportedBufferSize.Range(min = 256u, max = 512u),
                channels = 2u,
                minSampleRate = SampleRate(1u),
                maxSampleRate = SampleRate(22_050u),
                sampleFormat = SampleFormat.F32,
            ),
        )

        formats.sortWith { a, b -> a.cmpDefaultHeuristics(b) }

        assertEquals(SampleFormat.F32, formats[0].sampleFormat())
        assertEquals(SampleRate(1u), formats[0].minSampleRate())
        assertEquals(SampleRate(96_000u), formats[0].maxSampleRate())
        assertEquals(1u, formats[0].channels())

        assertEquals(SampleFormat.U16, formats[1].sampleFormat())
        assertEquals(SampleRate(1u), formats[1].minSampleRate())
        assertEquals(SampleRate(96_000u), formats[1].maxSampleRate())
        assertEquals(2u, formats[1].channels())

        assertEquals(SampleFormat.I16, formats[2].sampleFormat())
        assertEquals(SampleRate(1u), formats[2].minSampleRate())
        assertEquals(SampleRate(96_000u), formats[2].maxSampleRate())
        assertEquals(2u, formats[2].channels())

        assertEquals(SampleFormat.F32, formats[3].sampleFormat())
        assertEquals(SampleRate(1u), formats[3].minSampleRate())
        assertEquals(SampleRate(22_050u), formats[3].maxSampleRate())
        assertEquals(2u, formats[3].channels())

        assertEquals(SampleFormat.F32, formats[4].sampleFormat())
        assertEquals(SampleRate(1u), formats[4].minSampleRate())
        assertEquals(SampleRate(96_000u), formats[4].maxSampleRate())
        assertEquals(2u, formats[4].channels())
    }

    @Test
    fun testStreamInstant() {
        val a = StreamInstant.new(2, 0u)
        val b = StreamInstant.new(-2, 0u)
        val min = StreamInstant.new(Long.MIN_VALUE, 0u)
        val max = StreamInstant.new(Long.MAX_VALUE, 0u)

        assertEquals(StreamInstant.new(1, 0u), a.sub(1.seconds))
        assertEquals(StreamInstant.new(0, 0u), a.sub(2.seconds))
        assertEquals(StreamInstant.new(-1, 0u), a.sub(3.seconds))
        assertNull(min.sub(1.seconds))
        assertEquals(StreamInstant.new(-1, 0u), b.add(1.seconds))
        assertEquals(StreamInstant.new(0, 0u), b.add(2.seconds))
        assertEquals(StreamInstant.new(1, 0u), b.add(3.seconds))
        assertNull(max.add(1.seconds))
    }
}
