package com.example.letsmeet.addevent
import com.example.letsmeet.startup.Detail
import com.google.gson.annotations.SerializedName

class EventResponse (@SerializedName ("status")val status: Boolean,
               @SerializedName("message") val message:String,
               @SerializedName ("data")val data:ArrayList<EventStruct>,
                     @SerializedName ("detail")val detail: Detail
)

