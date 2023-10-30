package com.example.csapp


import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class Downloader {
    lateinit var context : Context
    suspend fun downloadFile(board_id : Long, context:Context) : String {
        this.context = context

        val strAuthHeader = "Bearer " + GlobalVariable.getAccessToken()
        if(strAuthHeader.length < 10) return "NoAccessToken"

        try{
            val response = withContext(Dispatchers.IO) {
                Log.i("download in Downloader", "download fx started")
                RetrofitScalarPlusObject.getApiService().download(strAuthHeader, board_id).execute()
            }

            if(response.isSuccessful){
                Log.i("download response", response.toString())
                val responseBody: ResponseBody? = response?.body()

                responseBody?.let{saveFileToMediaAndPlay(it) }

                return "Success"

            } else {
                return "ResponseNotSuccesful"
            }

        } catch(e:Exception) {
            return e.message.toString()

        }
    }

    private fun saveFileToMediaAndPlay(responseBody: ResponseBody): Boolean {
        return try {
            //val file = File(context.filesDir, "downloadedFile.wav")
            //val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "downloaded_audio.mp3")
            val file = ContentValues()
            file.put(MediaStore.Audio.Media.DISPLAY_NAME, "downloaded_audio.mp3")
            file.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")

            val audioUri = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, file)
            val outputStream = context.contentResolver.openOutputStream(audioUri!!)



            val inputStream = responseBody.byteStream()
            //val outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream?.write(buffer, 0, bytesRead)
            }

            outputStream?.flush()
            outputStream?.close()
            inputStream.close()
            Log.i("saveFileToInternalStorage", "file saved to downloadedFile.wav")

            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(context, audioUri)
            mediaPlayer.prepare()
            mediaPlayer.setOnCompletionListener { mp -> mp.release() }
            mediaPlayer.start()

            true
        } catch (e: Exception) {
            Log.i("saveFileToInternalStorage", e.message.toString())
            false
        }
    }

}