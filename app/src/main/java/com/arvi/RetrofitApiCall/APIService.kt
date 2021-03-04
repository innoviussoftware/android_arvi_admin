package com.arvi.RetrofitApiCall

import com.arvi.Model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface APIService {

    //todo:: login
    //get login
    @POST("v1/companies/users/login")
    fun getLogin(
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<GetLoginResponse>

    //todo:: add employee
    @POST("v1/companies/users/signup")
    fun addEmployee(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Body detail: JsonObject
    ): Call<GetAddEmployeeResponse>

    //todo:: add photo of new employee
    @POST("v1/companies/users/images?fromApp=1")
    @Multipart
    fun uploadUserPhoto(
        @Header("Authorization") auth: String,
        @Part file1: MultipartBody.Part
    ): Call<UploadPhotoResponse>

    //todo:: detect face
    @POST("v1/companies/face/detect?fromApp=1")
    @Multipart
    fun detectFace(
        @Header("Authorization") auth: String,
        @Part file1: MultipartBody.Part
    ): Call<DetectFaceNewResponse>

    //todo:: store data
    @POST("v1/companies/records")
    fun recordUserTemperature(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<ResponseBody>




    // get kiosk detail
    @GET("/kiosks/{kioskId}")
    fun getKiosk(
        @Path("kioskId") kioskId: String
    ): Call<GetKioskById>

    @POST("users/sendOtp")
    fun sendOtp(
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<SendOtpResponse>

    @POST("users/validateOtp")
    fun verifyOtp(
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<GetVerifyOtpResponse>

    @PUT("users/me")
    fun updateUserProfileDetail(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<UpdateUserDetailResponse>


    @POST("faces/search-faces-by-image/{kioskId}")
    @Multipart
    fun detectUserPhoto(
        @Part file1: MultipartBody.Part,
        @Path("kioskId") kioskId: String
    ): Call<DetectFaceResponse>


    //Todo: 23/2/2021... Add Arvi Admin Related API

    //Todo: visitors Related Flow.. Start...............................

    //1. add new visitor list
    @POST("v1/companies/visitor/entries")
    fun addVisitorsEntry(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<VisitorsListModel>

    //2. get visitors list : open=actuled, closed=screned
    @GET("v1/companies/visitor/entries")
    fun getVisitorsList(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Query("status") status:String
    ): Call<GetVisitorListResponse>

    //Check come visitor mobile number register or not in visitor list
    @GET("v1/companies/visitors")
    fun checkVisitorMobileNo(
        @Header("Authorization") auth: String,
        @Query("mobile") mobile:String
    ):Call<CheckMobileNoResponse>

    //id means entriesID,
    @POST("v1/companies/visitor/entries/{id}")
    fun visitorEntryRegister(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Path("id") id: Int,
        @Body detail: JsonObject
    ): Call<ResponseBody>


    @POST("v1/companies/visitor")
    fun newVisitorsEntryRegister(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Body detail: JsonObject
    ): Call<NewVisitorRegisterResponse>


//Todo: visitors Related Flow.. End...............................



    @GET("v1/companies/roles")
    fun getComaniesRolesList(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String
    ): Call<RoleListModel>

    @GET("v1/companies/roles")
    fun getDesignationList(
        @Header("Authorization") auth: String
    ):Call<GetDesignationListResponse>

    @GET("v1/companies/users")
    fun getComaniesUsersList(
        @Header("Authorization") auth: String
    ):Call<ResponseBody>

    //CHeck Mobile number thru VIsitor mobile no Register or not


}
