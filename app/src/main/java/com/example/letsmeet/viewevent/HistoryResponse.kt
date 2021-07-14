package com.example.letsmeet.viewevent
import com.example.letsmeet.startup.Detail
import com.google.gson.annotations.SerializedName

class HistoryResponse (@SerializedName ("status")val status: Boolean,
                       @SerializedName("message") val message:String,
                       @SerializedName ("data")val data:ArrayList<History>,
                       @SerializedName ("detail")val detail: Detail)

