package com.example.csapp

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.emitter.Emitter.Listener
import java.net.URISyntaxException
class SocketManager private constructor(){
    private var socket : Socket? = null

    init {
        try{
            val options = IO.Options()
            options.path = "/socket.io"
            socket = IO.socket(Cons.SOCKETIO_URL, options)
            //socket = IO.socket("http://192.168.200.182:3000/counsel", options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    companion object{
        private var instance : SocketManager? =  null

        fun getInstance() : SocketManager{
            if(instance == null){
                instance = SocketManager()
            }
            return instance!!
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun sendCreateRoomMessage(roomName : String) {
        socket?.emit("create_room", roomName)
    }

    fun sendLeaveRoomMessage(roomName : String){
        socket?.emit("leave_room", roomName)
    }

    fun addEventListener(event : String, listener: Emitter.Listener){
        socket?.on(event, listener)
    }

    fun removeEventListener(event : String, listener: Listener){
        socket?.off(event, listener)
    }

    fun sendUpdateBoardMessage(){
        socket?.emit("update_board", GlobalVariable.getUserName())
    }

    fun sendAudioStartMessage(){
        socket?.emit("customor_audio_start", GlobalVariable.getUserName())
    }

    fun sendAudioStopMessage(){
        socket?.emit("customor_audio_stop", GlobalVariable.getUserName())
    }

    fun sendAudioStream(audioData: ByteArray) {
        socket?.emit("audio_data", GlobalVariable.getUserName(), audioData)
    }

    fun sendCanvasData(boardData : String) {
        socket?.emit("canvas_data", GlobalVariable.getUserName(), boardData)
    }

}