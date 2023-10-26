package com.example.csapp

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
class SocketManager private constructor(){
    private var socket : Socket? = null

    init {
        try{
            val options = IO.Options()
            options.path = "/socket.io"
            // Set any additional options here if needed
            socket = IO.socket("http://10.100.203.29:3000/counsel", options)
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

}