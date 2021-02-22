package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import com.arvi.R
import com.arvi.Utils.SnackBar
import com.google.android.material.snackbar.Snackbar

class EnterCompanyDetailActivity : AppCompatActivity(), View.OnClickListener {


    var etCompanyNameECDA: EditText? = null
    var etAdminNameECDA: EditText? = null
    var etDesignationECDA: EditText? = null
    var etMobileNoECDA: EditText? = null
    var etMailECDA: EditText? = null
    var rlSubmitECDA: RelativeLayout? = null

    var context: Context? = null
    var snackbarView: View? = null
    var companyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_company_detail)
        setIds()
        setListeners()
    }

    private fun setListeners() {
        try {
            rlSubmitECDA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = EnterCompanyDetailActivity@ this
            snackbarView = findViewById(android.R.id.content)
            etCompanyNameECDA = findViewById(R.id.etCompanyNameECDA)
            etAdminNameECDA = findViewById(R.id.etAdminNameECDA)
            etDesignationECDA = findViewById(R.id.etDesignationECDA)
            etMobileNoECDA = findViewById(R.id.etMobileNoECDA)
            etMailECDA = findViewById(R.id.etMailECDA)
            rlSubmitECDA = findViewById(R.id.rlSubmitECDA)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.rlSubmitECDA -> {
                if (isValidInput()) {
                    var intent = Intent(context!!, EnterLoginDetailActivity::class.java)
                    intent.putExtra("companyId",companyId)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isValidInput(): Boolean {
        companyId = etCompanyNameECDA!!.text.toString()
        if (companyId.isNullOrEmpty()){
            etCompanyNameECDA!!.requestFocus()
            SnackBar.showValidationError(context!!,snackbarView!!,"Enter company Id")
            return false
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
