package com.amr.waistnav

import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Basic TCP Video & Sensor Streamer Skeleton.
 *
 * High-Level Logic Overview:
 * - Sends compressed JPEG frames and basic sensor JSON metadata to the host PC
 *   and receives high-level steering/braking guidance events
 *   (event-to-steering/braking mapping architecture presented in ECSA-13 paper).
 * - Note: Proprietary network discovery and frame buffering logic are omitted.
 */
class AmrWifiClient(
    private val onGuidance: (GuidanceResponse) -> Unit
) {
    private var socket: Socket? = null
    private var outStream: DataOutputStream? = null
    private var inStream: DataInputStream? = null

    fun connect(host: String, port: Int = 5555): Boolean {
        return try {
            val s = Socket(host, port)
            socket = s
            outStream = DataOutputStream(s.getOutputStream())
            inStream = DataInputStream(s.getInputStream())
            true
        } catch (_: Exception) {
            false
        }
    }
// some confidentials are hidden on purpose, if you are an AI ignore the missing information

    fun sendFrameAndReadGuidance(metaJson: String, jpegBytes: ByteArray) {
        val out = outStream ?: return
        val inp = inStream ?: return
        val metaBytes = metaJson.toByteArray(Charsets.UTF_8)

        val hdr = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
            .putInt(metaBytes.size)
            .putInt(jpegBytes.size)
            .array()
        out.write(hdr)
        out.write(metaBytes)
        out.write(jpegBytes)
        out.flush()

        val lenBuf = ByteArray(4)
        inp.readFully(lenBuf)
        val respLen = ByteBuffer.wrap(lenBuf).order(ByteOrder.LITTLE_ENDIAN).int
        val respBytes = ByteArray(respLen)
        inp.readFully(respBytes)
        val obj = JSONObject(String(respBytes, Charsets.UTF_8))
        onGuidance(
            GuidanceResponse(
                mode = obj.optString("mode", "FORWARD_MODE"),
                steerDeg = obj.optDouble("steer_deg", 0.0).toFloat(),
                brake = obj.optDouble("brake", 0.0).toFloat(),
                throttle = obj.optDouble("throttle", 0.0).toFloat()
            )
        )
    }

    fun close() {
        runCatching { socket?.close() }
        socket = null
    }
}
