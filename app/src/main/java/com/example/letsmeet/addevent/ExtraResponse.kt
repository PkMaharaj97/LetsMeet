package com.example.letsmeet.addevent

import com.google.gson.annotations.SerializedName

class ExtraResponse (@SerializedName("status")val status: Boolean,
    @SerializedName("message") val message:String,
    @SerializedName("data")val data:List<EventStruct>)