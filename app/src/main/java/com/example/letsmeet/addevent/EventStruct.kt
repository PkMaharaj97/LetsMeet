package com.example.letsmeet.addevent

import com.google.gson.annotations.SerializedName

class EventStruct (@SerializedName("Eid") var Eid:Int,
                    @SerializedName("Etitle")var Etitle:String,
                   @SerializedName("Edetails")var Edetails:String,
                   @SerializedName("Elocation") var Elocation:String,
                   @SerializedName("Etime") var Etime:String,
                   @SerializedName("Etype") var Etype:String,
                   @SerializedName("Estatus")var Estatus:String,
                   @SerializedName("Edate") var Edate:String,
                   @SerializedName("Efrom") var Efrom:String,
                   @SerializedName("Eto") var Eto:String,
                   @SerializedName("Eduration")  var Eduration:String,
                   @SerializedName("Ecreated_at")var Ecreated_at:String

)
