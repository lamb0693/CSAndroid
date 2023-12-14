package com.example.csapp.audiothread

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.example.csapp.util.SocketManager
import io.socket.emitter.Emitter


class AudioNetReceiver(private val context: Context) : Runnable {

    var bReceiving : Boolean = true

//    private val audioPlayer: MediaPlayer = MediaPlayer()
//
    private val SAMPLE_RATE : Int = 44100

    private lateinit var audioTrack : AudioTrack

    init {
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat( AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(1024)
            .build()

        audioTrack.play()


        SocketManager.getInstance().addEventListener("audio_data", onAudioData())
    }

    public fun stopReceiving() : Unit {
        bReceiving = false

    }

    override fun run() {
        Log.i("AudioReceivingThread", "started")

        while(bReceiving){

        }

        Log.i("AudioReceivingThread", "stopped")
    }

    inner class onAudioData : Emitter.Listener {
        override fun call(vararg args: Any?) {
            val audioData = args[0] as ByteArray
            Log.i("onAudioData", "$audioData")
            try {
                audioTrack.write(audioData, 0, audioData.size)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }

    }
}