package com.arvi.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class GetCalendarEventsResponse : ArrayList<GetCalendarEventsResponseItem>()

@Parcelize
data class GetCalendarEventsResponseItem(
    val A: GetCalendarEvent_A?=null,
    val HOL: GetCalendarEvent_HOL?=null,
    val L: GetCalendarEvent_L?=null,
    val MISS: GetCalendarEvent_MISS?=null,
    val P: GetCalendarEvent_P?=null,
    val VISITOR: GetCalendarEvent_VISITOR?=null,
    val date: String?=""
):Parcelable

@Parcelize
data class GetCalendarEvent_A(
    val color: String?="",
    val label: String?="",
    val metric: Int?=0,
    val text: String?=""
):Parcelable

@Parcelize
data class GetCalendarEvent_HOL(
    val color: String?="",
    val label: String?="",
    val metric: Int?=0,
    val text: String?=""
):Parcelable

@Parcelize
data class GetCalendarEvent_L(
    val color: String?="",
    val label: String?="",
    val metric: Int?=0,
    val text: String?=""
):Parcelable

@Parcelize
data class GetCalendarEvent_MISS(
    val color: String?="",
    val label: String?="",
    val metric: Int?=0,
    val text: String?=""
):Parcelable

@Parcelize
data class GetCalendarEvent_P(
    val color: String?="",
    val label: String?="",
    val metric: Int?=0,
    val text: String?=""
):Parcelable

@Parcelize
data class GetCalendarEvent_VISITOR(
    val color: String?="",
    val label: String?="",
    val metric: Int?=0,
    val text: String?=""
):Parcelable