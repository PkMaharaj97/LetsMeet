package com.example.letsmeet.startup

import com.google.gson.annotations.SerializedName

class Detail (@SerializedName("Requested")val Requested: Int,
              @SerializedName("Removed") val Removed:Int,
              @SerializedName("Accepted") val Accepted:Int,
              @SerializedName("Completed")val Completed: Int,
              @SerializedName("Added")val Added:Int,
              @SerializedName("Rescheduled")val Rescheduled:Int)

