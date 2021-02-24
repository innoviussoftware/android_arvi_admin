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

    @GET("v1/companies/roles")
    fun getComaniesRolesList(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String
    ): Call<RoleListModel>

    //visitors Related .. Start
    //get visitors list
    @GET("v1/companies/visitor/entries")
    fun getVisitorsList(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Query("pageSize") pageSize:Int,
        @Query("currentPage") currentPage:Int
    ): Call<VisitorsListModel>

    //add visitor list
    @POST("v1/companies/visitors")
    fun addVisitorsEntry(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<VisitorsListModel>
    //visitors Related .. End

    @POST("v1/companies/visitors/entry")
    fun updateVisitorsEntry(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Body detail: JsonObject
    ): Call<ResponseBody>


    @GET("v1/companies/roles")
    fun getDesignationList(
        @Header("Authorization") auth: String
    ):Call<GetDesignationListResponse>

}
