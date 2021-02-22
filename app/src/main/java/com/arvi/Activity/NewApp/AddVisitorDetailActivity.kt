package com.arvi.Activity.NewApp

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.arvi.R
import com.google.android.material.textfield.TextInputEditText
import java.text.DateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import com.crashlytics.android.Crashlytics
import androidx.core.content.ContextCompat.getSystemService
import com.arvi.Utils.KeyboardUtility
import com.arvi.Utils.SnackBar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_add_visitor_detail.view.*
import java.security.Key

import java.text.ParseException
import java.text.SimpleDateFormat


class AddVisitorDetailActivity : AppCompatActivity(), View.OnClickListener {


    var imgVwBackAVDA: ImageView? = null
    var etNameAVDA: TextInputEditText? = null
    var aTvToMeetAVDA: AutoCompleteTextView? = null
    var tILVisitDateAVDA: TextInputLayout? = null
    var etVisitDateAVDA: TextInputEditText? = null
    var tILVisitTimeAVDA: TextInputLayout? = null
    var etVisitTimeAVDA: TextInputEditText? = null
    var etComingFromAVDA: TextInputEditText? = null
    var etMobileAVDA: TextInputEditText? = null
    var etPurposeAVDA: TextInputEditText? = null
    var tvSaveAVDA: TextView? = null
    var llRegisterBtnAVDA: LinearLayout? = null
    var tvRegisterAVDA: TextView? = null
    var tvDeleteAVDA: TextView? = null


    var context: Context? = null
    var snackbarView: View? = null
    var from: String = "add"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visitor_detail)
        try {
            setIds()
            setListeners()
            if (intent.extras != null) {
                from = intent.getStringExtra("from")
                if (from.equals("list")) {
                    tvSaveAVDA!!.visibility = View.GONE
                    llRegisterBtnAVDA!!.visibility = View.VISIBLE
                } else {
                    tvSaveAVDA!!.visibility = View.VISIBLE
                    llRegisterBtnAVDA!!.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openDatePickerDialog() {
        try {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var showDay = "01"
                    var showMonth = "01"
                    if (dayOfMonth < 10) {
                        showDay = "0" + dayOfMonth
                    } else {
                        showDay = dayOfMonth.toString()
                    }

                    if (monthOfYear < 10) {
                        showMonth = "0" + monthOfYear
                    } else {
                        showMonth = monthOfYear.toString()
                    }
                    etVisitDateAVDA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                },
                year,
                month,
                day
            )

            dpd.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun openTimePickerDialog() {
        try {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    val time = "$selectedHour:$selectedMinute"

                    val fmt = SimpleDateFormat("HH:mm")
                    var date: Date? = null
                    try {
                        date = fmt.parse(time)
                    } catch (e: ParseException) {

                        e.printStackTrace()
                        Crashlytics.log(e.toString())
                    }

                    val fmtOut = SimpleDateFormat("hh:mm aa")

                    val formattedTime = fmtOut.format(date)
                    etVisitTimeAVDA!!.setText(formattedTime)
                }, hour, minute, false
            )//Yes 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.toString())
        }

    }

    private fun setListeners() {
        try {
            imgVwBackAVDA!!.setOnClickListener(this)
            tvSaveAVDA!!.setOnClickListener(this)
            tILVisitDateAVDA!!.setOnClickListener(this)
            etVisitDateAVDA!!.setOnClickListener(this)
            tILVisitTimeAVDA!!.setOnClickListener(this)
            etVisitTimeAVDA!!.setOnClickListener(this)
            tvRegisterAVDA!!.setOnClickListener(this)
            tvDeleteAVDA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        try {
            when (view!!.id) {
                R.id.imgVwBackAVDA -> {
                    KeyboardUtility.hideKeyboard(context!!, imgVwBackAVDA!!)
                    finish()
                }
                R.id.tvSaveAVDA -> {
                    finish()
                }
                R.id.tILVisitDateAVDA -> {
                    openDatePickerDialog()
                }
                R.id.etVisitDateAVDA -> {
                    openDatePickerDialog()
                }
                R.id.tILVisitTimeAVDA -> {
                    openTimePickerDialog()
                }
                R.id.etVisitTimeAVDA -> {
                    openTimePickerDialog()
                }
                R.id.tvRegisterAVDA -> {
                    openPersionVerifyDialog()

                }
                R.id.tvDeleteAVDA -> {
                    SnackBar.showInProgressError(context!!, snackbarView!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openPersionVerifyDialog() {
        try {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_verify_visitor)
            var imgVwPhotoDVV = dialog.findViewById(R.id.imgVwPhotoDVV) as ImageView
            var tvNameDVV = dialog.findViewById(R.id.tvNameDVV) as TextView
            var tvCompanyDVV = dialog.findViewById(R.id.tvCompanyDVV) as TextView
            var tvLastVisitedDVV = dialog.findViewById(R.id.tvLastVisitedDVV) as TextView
            var tvSamePersonDVV = dialog.findViewById(R.id.tvSamePersonDVV) as TextView
            var tvChangeDVV = dialog.findViewById(R.id.tvChangeDVV) as TextView
            tvSamePersonDVV.setOnClickListener {
                dialog.dismiss()
                openVisitorRegisterSuccessDialog()
            }
            tvChangeDVV.setOnClickListener {
                var intent = Intent(context!!, AddVisitorPhotoActivity::class.java)
                startActivity(intent)
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openVisitorRegisterSuccessDialog() {
        try {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_visitor_register_success)
            dialog.setCancelable(false)
            var imgVwPhotoDVRS = dialog.findViewById(R.id.imgVwPhotoDVRS) as ImageView
            var tvNameDVRS = dialog.findViewById(R.id.tvNameDVRS) as TextView
            var tvOkDVRS = dialog.findViewById(R.id.tvOkDVRS) as TextView
            tvOkDVRS.setOnClickListener {
                dialog.dismiss()
                finish()
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AddVisitorDetailActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackAVDA = findViewById(R.id.imgVwBackAVDA)
            etNameAVDA = findViewById(R.id.etNameAVDA)
            aTvToMeetAVDA = findViewById(R.id.aTvToMeetAVDA)
            tILVisitDateAVDA = findViewById(R.id.tILVisitDateAVDA)
            etVisitDateAVDA = findViewById(R.id.etVisitDateAVDA)
            tILVisitTimeAVDA = findViewById(R.id.tILVisitTimeAVDA)
            etVisitTimeAVDA = findViewById(R.id.etVisitTimeAVDA)
            etComingFromAVDA = findViewById(R.id.etComingFromAVDA)
            etMobileAVDA = findViewById(R.id.etMobileAVDA)
            etPurposeAVDA = findViewById(R.id.etPurposeAVDA)
            tvSaveAVDA = findViewById(R.id.tvSaveAVDA)
            llRegisterBtnAVDA = findViewById(R.id.llRegisterBtnAVDA)
            tvRegisterAVDA = findViewById(R.id.tvRegisterAVDA)
            tvDeleteAVDA = findViewById(R.id.tvDeleteAVDA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        KeyboardUtility.hideKeyboard(context!!, imgVwBackAVDA!!)
        finish()
    }
}
