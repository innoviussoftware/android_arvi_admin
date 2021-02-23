package com.arvi.Activity.NewApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.arvi.R

class EnterEmployeeDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee_details)
    }
    
    
    fun backAddEmpolyee(view: View){
        finish()
    }
}