package com.arvi.RetrofitApiCall

import com.arvi.Adapter.GetLeaveRequestResponse
import com.arvi.Model.*
import com.arvihealthscanner.Model.GetEmployeeListResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface APIService {

    //todo:: login
    //get login
    @POST("companies/users/login?fromApp=1")
    fun getLogin(
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<GetLoginResponse>

    //todo:: add employee old
    @POST("companies/users/signup")
    fun addEmployee(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Body detail: JsonObject
    ): Call<GetAddEmployeeResponse>

    //todo:: get employee list
    @GET("companies/users")
    fun getComaniesUsersList(
        @Header("Authorization") auth: String
    ):Call<GetEmployeeListResponse>


    //todo:: get employee detail
    @GET("companies/users/{id}")
    fun getEmpDetail(
        @Header("Authorization") auth: String,
        @Path("id")id: Int
    ):Call<GetEmpDetailResponse>

    //todo:: create employee
    @POST("companies/employees")
    fun createEmployee(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Body detail: JsonObject
    ): Call<ResponseBody>

    //todo:: update employee detail
    @PUT("companies/employees/{id}")
    fun updateEmployee(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Path("id")id:Int,
        @Body detail: JsonObject
    ): Call<ResponseBody>




    //todo:: add photo of new employee
    @POST("companies/users/images?fromApp=1")
    @Multipart
    fun uploadUserPhoto(
        @Header("Authorization") auth: String,
        @Part file1: MultipartBody.Part
    ): Call<UploadPhotoResponse>

    //todo:: detect face
    @POST("companies/face/detect?fromApp=1")
    @Multipart
    fun detectFace(
        @Header("Authorization") auth: String,
        @Part file1: MultipartBody.Part
    ): Call<DetectFaceNewResponse>

    //todo:: store data
    @POST("companies/records")
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
    @POST("companies/visitor/entries")
    fun addVisitorsEntry(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<VisitorsListModel>

    //2. get visitors list : open=expected, closed=screened
    @GET("companies/visitor/entries")
    fun getVisitorsList(
        @Header("Content-Type") ctype: String,
        @Header("Authorization") auth: String,
        @Query("status") status:String
    ): Call<GetVisitorListResponse>

    //Check come visitor mobile number register or not in visitor list
    @GET("companies/visitors")
    fun checkVisitorMobileNo(
        @Header("Authorization") auth: String,
        @Query("mobile") mobile:String
    ):Call<CheckMobileNoResponse>

    //id means entriesID,
    @POST("companies/visitor/entries/{id}")
    fun visitorEntryRegister(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Path("id") id: Int,
        @Body detail: JsonObject
    ): Call<ResponseBody>


    @POST("companies/visitors")
    fun newVisitorsEntryRegister(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ): Call<NewVisitorRegisterResponse>

    //todo:: delete visitor
    @DELETE("companies/visitor/entries/{id}")
    fun deleteVisitor(
        @Header("Authorization") auth: String,
        @Path("id") id: Int
    ):Call<ResponseBody>


//Todo: visitors Related Flow.. End...............................


//todo:: employee add APi start.....................................
    @GET("companies/roles")
    fun getDesignationList(
        @Header("Authorization") auth: String
    ):Call<GetDesignationListResponse>

    @GET("companies/groups")
    fun getGroupList(
        @Header("Authorization") auth: String
    ):Call<GetGroupListResponse>


    //todo:: employee add APi end.....................................

    //todo:: add leave request
    @POST("attendence/leaves")
    fun addLeaveRequest(
        @Header("Authorization") auth: String,
        @Header("Content-Type") ctype: String,
        @Body detail: JsonObject
    ):Call<ResponseBody>

    //todo:: get leave request
    @GET("attendence/leaves")
    fun getLeaveRequest(
        @Header("Authorization") auth: String
    ):Call<GetLeaveRequestResponse>

    //todo:: check dynamic url is valid
    @GET("ping")
    fun checkBaseURL():Call<ResponseBody>



    //todo:: get attendance summary
    @GET("dashboard/attendance_summary")
    fun getAttendanceSummary(
        @Header("Authorization") auth:String,
        @Query("startDate") startDate:String,
        @Query("endDate")endDate:String,
        @Query("groupId")groupId:Int
    ):Call<GetAttendanceSummaryResponse>

    //todo:: get Key metrics
    @GET("dashboard/key_metrics")
    fun getKeyMetrics(
        @Header("Authorization") auth:String,
        @Query("startDate") startDate:String,
        @Query("endDate")endDate:String,
        @Query("groupId")groupId:Int
    ):Call<GetKeyMetricsResponse>

    //todo:: get regularisation list
    @GET("attendence")
    fun getRegularisationlist(
        @Header("Authorization") auth:String,
        @Query("date")date:String
    ):Call<GetRegularisationRequestResponse>

    //todo:: add regularisation
    @PATCH("attendence/{id}")
    fun updateRegularisationRequest(
        @Header("Authorization") auth:String,
        @Header("Content-Type") ctype:String,
        @Path("id")id:Int,
        @Body detail: JsonObject
    ):Call<ResponseBody>

    //todo:: get work shift list
    @GET("attendence/workshifts")
    fun getWorkShifts(
        @Header("Authorization") auth:String
    ):Call<GetWorkShiftListResponse>

    //todo:: get calendar event  = all data
    @GET("dashboard/calender_events")
    fun getCalendarEvent(
        @Header("Authorization") auth:String,
        @Query("fromApp")fromApp:Int,
        @Query("startDate") startDate:String,
        @Query("endDate")endDate:String
    ):Call<GetCalendarEventsResponse>

    //todo:: get calendar event with group filter
    @GET("dashboard/calender_events")
    fun getCalendarEventWithGroup(
        @Header("Authorization") auth:String,
        @Query("fromApp")fromApp:Int,
        @Query("startDate") startDate:String,
        @Query("endDate")endDate:String,
        @Query("groupId")groupId:Int
    ):Call<GetCalendarEventsResponse>
}
