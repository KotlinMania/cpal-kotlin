package io.github.kotlinmania.cpal.host.nullhost

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NullTest {
    @Test
    fun testNullHost() {
        val hostResult = NullHost.new()
        assertTrue(hostResult.isSuccess)
        val host = hostResult.getOrThrow()
        assertFalse(host.isAvailable())
        assertNull(host.defaultInputDevice())
        assertNull(host.defaultOutputDevice())

        val devices = host.devices().getOrThrow()
        assertEquals(0, devices.toList().size)
    }

    @Test
    fun testNullDevice() {
        val device = NullDevice()
        assertEquals("null", device.name().getOrThrow())
        assertTrue(device.defaultInputConfig().isFailure)
        assertTrue(device.defaultOutputConfig().isFailure)
    }

    @Test
    fun testNullStream() {
        val stream = NullStream()
        assertTrue(stream.play().isFailure)
        assertTrue(stream.pause().isFailure)
    }
}
