package com.arvi.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetAllDataAttendanceAdapter

import com.arvi.R
import java.util.*


class Dashboard_AllDataFragment : Fragment(), View.OnClickListener {


    var etStartDateADDF: EditText? = null
    var etEndDateADDF: EditText? = null
    var tvApplyADDF: TextView? = null
    var imgVwFilterADDF: ImageView? = null
    var etEmpNameADDF: EditText? = null
    var rVwAddDataADDF: RecyclerView? = null

    var appContext: Context? = null
    var snackbarView: View? = null

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
        var view = inflater.inflate(R.layout.fragment_dashboard__all_data, container, false)
        setIds(view)
        setListeners()
        setData()
        return view
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            var setVisitorDataAdapter = SetAllDataAttendanceAdapter(appContext!!)
            rVwAddDataADDF!!.layoutManager =
                LinearLayoutManager(appContext, LinearLayout.VERTICAL, false)
            rVwAddDataADDF!!.setAdapter(setVisitorDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            etStartDateADDF!!.setOnClickListener(this)
            etEndDateADDF!!.setOnClickListener(this)
            tvApplyADDF!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.etStartDateADDF -> {
                getDate("startDate")
            }
            R.id.etEndDateADDF -> {
            }
            R.id.tvApplyADDF -> {
            }
        }
    }

    private fun getDate(from: String) {
        try {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                appContext!!,
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
                    if(from.equals("startDate")) {
                        etStartDateADDF!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    }else{
                        etEndDateADDF!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    }
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

    private fun setIds(view: View) {
        appContext = activity
        snackbarView = activity?.findViewById(android.R.id.content)

        etStartDateADDF = view.findViewById(R.id.etStartDateADDF)
        etEndDateADDF = view.findViewById(R.id.etEndDateADDF)
        tvApplyADDF = view.findViewById(R.id.tvApplyADDF)
        imgVwFilterADDF = view.findViewById(R.id.imgVwFilterADDF)
        etEmpNameADDF = view.findViewById(R.id.etEmpNameADDF)
        rVwAddDataADDF = view.findViewById(R.id.rVwAddDataADDF)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Dashboard_AllDataFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
