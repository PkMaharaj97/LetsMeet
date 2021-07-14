package com.example.letsmeet

import com.google.gson.annotations.SerializedName

class Contacts(@SerializedName("Pic") var Pic: Int,
               @SerializedName ("Name") var Name: String,
               @SerializedName ("Email") var Email: String,
               @SerializedName ("Location") var Location: String,
               @SerializedName ("Phone") var Phone: String)






