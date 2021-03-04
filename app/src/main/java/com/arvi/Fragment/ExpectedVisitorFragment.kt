package com.arvi.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddVisitorDetailActivity
import com.arvi.Activity.NewApp.EnterCompanyDetailActivity
import com.arvi.Adapter.SetVisitorDataAdapter
import com.arvi.Model.Result
import com.arvi.Model.GetVisitorListResponse
import com.arvi.Model.GetVisitorListResult
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpectedVisitorFragment : Fragment(), View.OnClickListener {

    var appContext: Context? = null
    var snackbarView: View? = null


    var rVwVisitorFEV: RecyclerView? = null
    var tvNoVisitorFEV: TextView? = null
    var imgVwAddVisitorFEV: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_expected_visitor, container, false)
        setIds(view)
        setListeners()

        if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
            callGetVisitorListAPI()
        } else {
            SnackBar.showInternetError(appContext!!, snackbarView!!)
        }

        return view
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = ExpectedVisitorFragment()
    }


    private fun setIds(view: View) {
        try {
            appContext = activity
            snackbarView = activity?.findViewById(android.R.id.content)
            rVwVisitorFEV = view.findViewById(R.id.rVwVisitorFEV)
            tvNoVisitorFEV = view.findViewById(R.id.tvNoVisitorFEV)
            imgVwAddVisitorFEV = view.findViewById(R.id.imgVwAddVisitorFEV)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setListeners() {
        try {
            imgVwAddVisitorFEV!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.imgVwAddVisitorFEV -> {
                    var intent = Intent(appContext, AddVisitorDetailActivity::class.java)
                    startActivityForResult(intent, REQUEST_VISITOR)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var totalPageSize: Int = 0
    var currentPage: Int = 1

    lateinit var alVisitorList: ArrayList<GetVisitorListResult>

    private fun callGetVisitorListAPI() {
        try {

            var token = AppConstants.BEARER_TOKEN + SessionManager.getToken(appContext!!)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//{{hostname}}/v1/companies/visitor/entries?status=open
            mAPIService!!.getVisitorsList("application/json",token,"open")
                .enqueue(object : Callback<GetVisitorListResponse> {
                    override fun onResponse(
                        call: Call<GetVisitorListResponse>,
                        response: Response<GetVisitorListResponse>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                alVisitorList = ArrayList()
                                alVisitorList.addAll(response.body().result!!)
                                setVisitorData(alVisitorList)
                            } else if (response.code() == 401) {
                                var intent = Intent(appContext!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(appContext!!)
                            }else {
                                SnackBar.showError(
                                    context!!,
                                    snackbarView!!,
                                    "Something went wrong"
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<GetVisitorListResponse>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }
    }


    public val REQUEST_VISITOR = 1555
    private fun setVisitorData(alVisitorList: ArrayList<GetVisitorListResult>) {
        try {

            if (alVisitorList.size > 0) {
                tvNoVisitorFEV!!.visibility=View.GONE
                rVwVisitorFEV!!.visibility=View.VISIBLE

                var setVisitorDataAdapter = SetVisitorDataAdapter(appContext!!, alVisitorList,
                    object : SetVisitorDataAdapter.BtnClickListener {
                        override fun onVisitorDetailsBtnClick(position: Int) {
                            val gson = Gson()
                            var myJson = gson.toJson(alVisitorList[position])

                            var intent = Intent(context, AddVisitorDetailActivity::class.java)
                            intent.putExtra("from", "list")
                            intent.putExtra("visitorData", myJson)
                            startActivityForResult(intent, REQUEST_VISITOR)
                        }
                    })

                rVwVisitorFEV!!.layoutManager =
                    LinearLayoutManager(appContext, RecyclerView.VERTICAL, false)
                rVwVisitorFEV!!.adapter = setVisitorDataAdapter
            }else{
                tvNoVisitorFEV!!.visibility=View.VISIBLE
                rVwVisitorFEV!!.visibility=View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VISITOR) {
            if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                callGetVisitorListAPI()
            } else {
                SnackBar.showInternetError(appContext!!, snackbarView!!)
            }
        }
    }


}