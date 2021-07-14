package com.example.letsmeet.addevent

import com.example.letsmeet.startup.Message
import com.example.letsmeet.viewevent.HistoryResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiInterface
{
    @FormUrlEncoded
    @POST("AddEvent.php")
fun AddEvent(
        @Field("Etitle") Etitle:String,
        @Field("Edetails") Edetails:String,
        @Field("Elocation")  Elocation:String,
        @Field("Etime")  Etime:String,
        @Field("Etype")  Etype:String,
        @Field("Estatus") Estatus:String,
        @Field("Edate")  Edate:String,
        @Field("Efrom")  Efrom:String,
        @Field("Eto")  Eto:String,
        @Field("Eduration")   Eduration:String
    ): Call<EventResponse> // body data

    @FormUrlEncoded
    @POST("EventList.php")
    fun GetEventList(
        @Field("Username") Username:String): Call<EventResponse>
 /* @GET("SentEventList.php")
  fun GetEventList(): Call<EventResponse>*/

    @GET("ReceivedEventList.php")
    fun GetRecEventList(): Call<EventResponse>

    @FormUrlEncoded
    @POST("getschedule.php")
    fun getschedule(
        @Field("Username") Username:String,
        @Field("Date") Date:String): Call<ExtraResponse>

    @FormUrlEncoded
    @POST("updatestatus.php")
    fun changeststus(
        @Field("Id") Id:Int,
        @Field("Status") Status:String): Call<EventResponse>

    @FormUrlEncoded
    @POST("reschedule.php")
    fun rescheduleEvent(
        @Field("Eid") Eid:Int,
        @Field("Etitle") Etitle:String,
        @Field("Edetails") Edetails:String,
        @Field("Elocation")  Elocation:String,
        @Field("Etime")  Etime:String,
        @Field("Etype")  Etype:String,
        @Field("Estatus") Estatus:String,
        @Field("Edate")  Edate:String,
        @Field("Efrom")  Efrom:String,
        @Field("Eto")  Eto:String,
        @Field("Eduration")   Eduration:String
    ): Call<EventResponse> // body data
    @FormUrlEncoded
    @POST("Registration.php")
    fun doRegister(
        @Field("Id")Id:String,
        @Field("Name")Name:String,
        @Field("Email")Email:String,
        @Field("Phone")Phone:String,
        @Field("Location")Location:String,
        @Field("Password")Password:String): Call<Message> // body data

    @FormUrlEncoded
    @POST("Login.php")
    fun doLogin(
        @Field("Email") Email: String,
        @Field("Password") Password: String): Call<Message> // body data

    @GET("Userlist.php")
    fun getuserlist(): Call<Message>


    @FormUrlEncoded
    @POST("gethistory.php")
    fun getHistory(
        @Field("Id") Id:Int): Call<HistoryResponse>


}