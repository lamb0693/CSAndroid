package com.example.csapp


import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.csapp.ui.drawimage.DrawImageActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    suspend fun downloadFile(board_id : Long, content : String, context:Context) : String {
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

                if(content.equals("AUDIO")){
                    responseBody?.let{saveAudioFileToMediaAndPlay(it) }
                } else if (content.equals("PAINT")){
                    responseBody?.let{savePaintFileAndStartActivity(it)}
                } else if (content.equals("IMAGE")) {
                    responseBody?.let{saveImageFileAndShowDialog(it)}
                }

                return "Success"

            } else {
                return "ResponseNotSuccesful"
            }

        } catch(e:Exception) {
            return e.message.toString()

        }
    }

    private fun saveAudioFileToMediaAndPlay(responseBody: ResponseBody): Boolean {
        return try {
            //val file = File(context.filesDir, "downloadedFile.wav")
            //val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "downloaded_audio.mp3")
            val file = ContentValues()
            val dwonFileName = "audio" + System.currentTimeMillis() + ".wav"
            file.put(MediaStore.Audio.Media.DISPLAY_NAME, dwonFileName)
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
            Log.i("saveFileToInternalStorage", "file saved to $dwonFileName")

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

    private fun savePaintFileAndStartActivity(responseBody: ResponseBody): Boolean {
        return try {

            val file = File(context.filesDir, "downloaded_paint.json")

            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            //  파일이름을 가지고 Intent를 시행
            val paintIntent = Intent(context,  DrawImageActivity::class.java )
            paintIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            paintIntent.putExtra("paint_file_path", file.absolutePath)
            context.startActivity(paintIntent)

            true
        } catch (e: Exception) {
            Log.i("savePaintFileAndStartActivity", e.message.toString())
            false
        }
    }

    suspend fun saveImageFileAndShowDialog(responseBody: ResponseBody): Boolean {
        return try {
            withContext(Dispatchers.Main) {
                val imageDialog = CustomImageDialog(context)
                val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())

                imageDialog.imageView.setImageBitmap(bitmap)
                imageDialog.show()
            }


//            val imageDialog = CustomImageDialog(context)
//
//            Glide.with(context)
//                .asBitmap()
//                .load(responseBody.byteStream())
//                .into(imageDialog.imageView)
//            CoroutineScope(Dispatchers.Main).launch {
//                imageDialog.show()
//            }
            true
        } catch (e: Exception) {
            Log.i("saveImageFileAndShowDialog", e.message.toString())
            false
        }
    }

}