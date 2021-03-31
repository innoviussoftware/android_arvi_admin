package com.arvi.Model

class GetCalendarEventsResponse : ArrayList<GetCalendarEventsResponseItem>()

data class GetCalendarEventsResponseItem(
    val A: GetCalendarEvent_A,
    val HOL: GetCalendarEvent_HOL,
    val L: GetCalendarEvent_L,
    val MISS: GetCalendarEvent_MISS,
    val P: GetCalendarEvent_P,
    val VISITOR: GetCalendarEvent_VISITOR,
    val date: String
)

data class GetCalendarEvent_A(
    val color: String,
    val label: String,
    val metric: Int,
    val text: String
)

data class GetCalendarEvent_HOL(
    val color: String,
    val label: String,
    val metric: Int,
    val text: String
)

data class GetCalendarEvent_L(
    val color: String,
    val label: String,
    val metric: Int,
    val text: String
)

data class GetCalendarEvent_MISS(
    val color: String,
    val label: String,
    val metric: Int,
    val text: String
)

data class GetCalendarEvent_P(
    val color: String,
    val label: String,
    val metric: Int,
    val text: String
)

data class GetCalendarEvent_VISITOR(
    val color: String,
    val label: String,
    val metric: Int,
    val text: String
)