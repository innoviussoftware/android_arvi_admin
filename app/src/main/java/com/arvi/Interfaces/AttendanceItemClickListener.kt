package com.arvi.Interfaces

import android.view.View

interface AttendanceItemClickListener {
    abstract fun onClick(view: View, position: Int,from:String)
}
