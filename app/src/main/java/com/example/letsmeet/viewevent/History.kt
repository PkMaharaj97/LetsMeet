package com.example.letsmeet.viewevent

import com.google.gson.annotations.SerializedName

class History(
    @SerializedName("Hid")var Hid:Int,
    @SerializedName ("Status") var Status:String,
    @SerializedName ("Eid") var Eid:Int,
    @SerializedName ("Changed_at")  var Changed_at: String)

