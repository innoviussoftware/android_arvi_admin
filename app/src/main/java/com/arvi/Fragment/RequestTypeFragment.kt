package com.arvi.Fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.arvi.Activity.NewApp.LeaveRequestListActivity
import com.arvi.Activity.NewApp.RegularizationRequestListActivity
import com.arvi.Activity.NewApp.WfHRequestListActivity

import com.arvi.R


class RequestTypeFragment : Fragment(), View.OnClickListener {

    var llRegularizationRTF: LinearLayout? = null
    var llWfHRTF: LinearLayout? = null
    var llLeaveRequestRTF: LinearLayout? = null

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
        var view = inflater.inflate(R.layout.fragment_request_type, container, false)
        setIds(view)
        setListeners()
        return view
    }

    private fun setListeners() {
        llRegularizationRTF!!.setOnClickListener(this)
        llWfHRTF!!.setOnClickListener(this)
        llLeaveRequestRTF!!.setOnClickListener(this)
    }

    private fun setIds(view: View) {
        appContext = activity
        snackbarView = activity?.findViewById(android.R.id.content)
        llLeaveRequestRTF = view.findViewById(R.id.llLeaveRequestRTF)
        llRegularizationRTF = view.findViewById(R.id.llRegularizationRTF)
        llWfHRTF = view.findViewById(R.id.llWfHRTF)
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
            RequestTypeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.llRegularizationRTF -> {
                var intent = Intent(appContext, RegularizationRequestListActivity::class.java)
                startActivity(intent)
            }
            R.id.llLeaveRequestRTF -> {
                var intent = Intent(appContext, LeaveRequestListActivity::class.java)
                startActivity(intent)
            }
            R.id.llWfHRTF -> {
                var intent = Intent(appContext, WfHRequestListActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
