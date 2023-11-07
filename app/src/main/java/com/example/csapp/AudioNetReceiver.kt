package com.example.csapp

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import io.socket.emitter.Emitter
import java.io.File
import java.io.FileOutputStream


class AudioNetReceiver(private val context: Context) : Runnable {

    var bReceiving : Boolean = true

    private lateinit var audioPlayer : MediaPlayer

    private val SAMPLE_RATE : Int = 44100

    init {
        audioPlayer = MediaPlayer()
        SocketManager.getInstance().addEventListener("audio_data", onAudioData())
    }

    public fun stopReceiving() : Unit {
        bReceiving = false
        audioPlayer.stop()
        audioPlayer.reset()
        audioPlayer.release()
    }

    override fun run() {
        Log.i("AudioReceivingThread", "started")

        while(bReceiving){

        }

        Log.i("AudioReceivingThread", "stopped")
    }

    private fun generateWavHeader(dataSize: Int): ByteArray {
        val header = ByteArray(44)
        val totalDataLen = dataSize + 36
        val bitrate = SAMPLE_RATE * 16 * 1
        val byteRate = bitrate / 8

        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = 0x10
        header[16] = 0
        header[17] = 0
        header[18] = 0
        header[19] = 1
        header[20] = 0
        header[21] = 1
        header[22] = 0
        header[23] = 0
        header[24] = (SAMPLE_RATE and 0xff).toByte()
        header[25] = (SAMPLE_RATE shr 8 and 0xff).toByte()
        header[26] = (SAMPLE_RATE shr 16 and 0xff).toByte()
        header[27] = (SAMPLE_RATE shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = 2
        header[33] = 0
        header[34] = 16
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (dataSize and 0xff).toByte()
        header[41] = (dataSize shr 8 and 0xff).toByte()
        header[42] = (dataSize shr 16 and 0xff).toByte()
        header[43] = (dataSize shr 24 and 0xff).toByte()
        return header
    }

    inner class onAudioData : Emitter.Listener {
        override fun call(vararg args: Any?) {
            try{
                val audioData = args[0] as ByteArray
                Log.i("onAudioData", "$audioData")

                // Create a temporary WAV file from PCM data
//                val tempWavFile = File(context.cacheDir, "temp_audio.wav")
//                val outputStream = FileOutputStream(tempWavFile)
//                outputStream.write(generateWavHeader(audioData.size))
//                outputStream.write(audioData)
//                outputStream.close()

                // Reset and release MediaPlayer if it's already playing
//                if (audioPlayer.isPlaying) {
//                    audioPlayer.reset()
//                }

                // Set the WAV file as the data source for the MediaPlayer
//                audioPlayer.setDataSource(tempWavFile.absolutePath)
//                audioPlayer.prepare()
//                audioPlayer.start()
            } catch (e: Exception) {
                Log.e("onAudioData", "${e.message}")
            }

        }

    }
}