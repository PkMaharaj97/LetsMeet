package com.example.letsmeet.startup
import com.google.gson.annotations.SerializedName

class Message (@SerializedName ("status")val status: Boolean,
               @SerializedName("message") val message:String,
               @SerializedName ("data")val data: Users)