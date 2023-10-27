package com.example.csapp

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AudioNetStreamer(private val context: Context) : Runnable {
    private var threadStoppedListener: AudioThreadStoppedListener? = null

    private var bStreaming = true
    private var audioRecord: AudioRecord? = null
    private val audioBufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    fun setThreadStoppedListener(listener: AudioThreadStoppedListener) {
        this.threadStoppedListener = listener
    }

    private val mediaPlayer = MediaPlayer()

    fun stopStreaming(){
        bStreaming = false
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        Log.i(">>> audionetprocess", "started")
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            audioBufferSize
        )
        audioRecord?.startRecording()

        val audioFileName = "audio_sample.raw"
        val audioFile = File(context.filesDir, audioFileName) // Use the application's private files directory

        val audioOutputStream = FileOutputStream(audioFile)

        val audioData = ByteArray(audioBufferSize)

        while (bStreaming) {
            val bytesRead = audioRecord?.read(audioData, 0, audioBufferSize) ?: 0
            if (bytesRead > 0) {
                // 서버로 보내기

                // file로 저장
                GlobalScope.launch {
                    writeAudioDataToFile(audioData, audioOutputStream)
                }
            }
        }

        val wavFile = File(context.filesDir, "audio_sample.wav")

        addWavHeader(audioFile, wavFile)

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        threadStoppedListener?.onAudioThreadStopped()

        Log.i(">>> audionetprocess", "stopped")

    }

    suspend fun writeAudioDataToFile(audioData: ByteArray, audioOutputStream: FileOutputStream) {
        audioOutputStream.write(audioData)
        audioOutputStream.flush()
    }

    fun addWavHeader(inputFile: File, outputFile: File) {
        val dataSize = inputFile.length()
        val channels = 1 // Mono
        val sampleRate = 44100
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val header = ByteArray(44)

        val totalDataLen = dataSize + 36
        val totalAudioLen = dataSize

        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xFF).toByte()
        header[5] = (totalDataLen shr 8 and 0xFF).toByte()
        header[6] = (totalDataLen shr 16 and 0xFF).toByte()
        header[7] = (totalDataLen shr 24 and 0xFF).toByte()

        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()

        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // Size of the format chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // Audio format (1 for PCM)
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xFF).toByte()
        header[25] = (sampleRate shr 8 and 0xFF).toByte()
        header[26] = (sampleRate shr 16 and 0xFF).toByte()
        header[27] = (sampleRate shr 24 and 0xFF).toByte()
        header[28] = (byteRate and 0xFF).toByte()
        header[29] = (byteRate shr 8 and 0xFF).toByte()
        header[30] = (byteRate shr 16 and 0xFF).toByte()
        header[31] = (byteRate shr 24 and 0xFF).toByte()
        header[32] = blockAlign.toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0

        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xFF).toByte()
        header[41] = (totalAudioLen shr 8 and 0xFF).toByte()
        header[42] = (totalAudioLen shr 16 and 0xFF).toByte()
        header[43] = (totalAudioLen shr 24 and 0xFF).toByte()

        FileInputStream(inputFile).use { inputStream ->
            FileOutputStream(outputFile).use { outputStream ->
                outputStream.write(header)
                inputStream.copyTo(outputStream)
            }
        }
    }

}