package com.arvi.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddWfHRequestActivity
import com.arvi.Adapter.SetWfHDataAdapter
import com.arvi.R
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.SnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class WFHListFragment : Fragment(), View.OnClickListener {

    var rVwRequestWfhRA: RecyclerView?=null
    var tvNoVisitorWfhRA: TextView?=null
    var imgVwAddRequestWfhRA: FloatingActionButton?=null

    var imgVwCalendarWF:ImageView?=null

    var appContext: Context?=null
    var snackbarView: View?=null
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    var startDate: String? = null
    var endDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_w_f_h_list, container, false)
        appContext = activity
        snackbarView = view.findViewById(android.R.id.content)
        rVwRequestWfhRA = view.findViewById(R.id.rVwRequestWfhRA)
        tvNoVisitorWfhRA = view.findViewById(R.id.tvNoVisitorWfhRA)
        imgVwAddRequestWfhRA = view.findViewById(R.id.imgVwAddRequestWfhRA)
        imgVwCalendarWF = view.findViewById(R.id.imgVwCalendarWF)
        imgVwAddRequestWfhRA!!.setOnClickListener(this)
        imgVwCalendarWF!!.setOnClickListener(this)
        setData()
        return  view
    }


    override fun onResume() {
        super.onResume()
        getDefaultDate()
    }

    private fun getDefaultDate() {
        try {
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, 0)
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            val monthFirstDay: Date = calendar.getTime()
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val monthLastDay: Date = calendar.getTime()

            val df = SimpleDateFormat("yyyy-MM-dd")
            startDate = df.format(monthFirstDay)
            endDate = df.format(monthLastDay)

            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            Log.e("DateFirstLast", "$startDate $endDate")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            var setDataAdapter = SetWfHDataAdapter(appContext!!)
            rVwRequestWfhRA!!.layoutManager =
                LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rVwRequestWfhRA!!.setAdapter(setDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            WFHListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun openCalendarView() {
        try {
            val datePickerDialog = DatePickerDialog(
                appContext!!,
                android.R.style.Theme_Holo_Dialog,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    startDate =
                        year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()

    /*                if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                        callGetRegularizationListAPI()
                    } else {
                        SnackBar.showInternetError(appContext!!, snackbarView!!)
                    }
*/

                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()

            val years = datePickerDialog.findViewById<View>(
                Resources.getSystem().getIdentifier("android:id/day", null, null)
            )
            if (years != null) {
                years.visibility = View.GONE
            }
            datePickerDialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
            datePickerDialog.window!!.setGravity(Gravity.CENTER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onClick(view1: View?) {
        try {
            when(view1!!.id){

                R.id.imgVwAddRequestWfhRA->{
                    var intent = Intent(appContext!!, AddWfHRequestActivity::class.java)
                    startActivity(intent)
                }
                R.id.imgVwCalendarWF->{
                    openCalendarView()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}