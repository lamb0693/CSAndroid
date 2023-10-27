package com.example.csapp

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class AudioNetStreamer(private val context: Context) : Runnable {
    private var bStreaming = true
    private var audioRecord: AudioRecord? = null
    private val audioBufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

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

        val audioFileName = "audio_sample.wav" // Change the file name as needed
        val audioFile = File(context.filesDir, audioFileName) // Use the application's private files directory

        val audioOutputStream = FileOutputStream(audioFile)

        val audioData = ByteArray(audioBufferSize)

        while (bStreaming) {
            val bytesRead = audioRecord?.read(audioData, 0, audioBufferSize) ?: 0
            if (bytesRead > 0) {
                // 서버로 보내기

                // file로 저장
                audioOutputStream.write(audioData, 0, bytesRead)
            }
        }

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        Log.i(">>> audionetprocess", "stopped")

    }

}