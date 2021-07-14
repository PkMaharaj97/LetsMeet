package com.example.letsmeet.startup
import com.google.gson.annotations.SerializedName
class Users(@SerializedName ("Id")var Id: String,
            @SerializedName ("Name")var Name: String,
            @SerializedName ("Email") var Email: String,
            @SerializedName ("Password")var Password: String,
            @SerializedName ("Location")var Location: String,
            @SerializedName ("Phone")var Phone: String)






