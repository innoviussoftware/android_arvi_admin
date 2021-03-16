package com.arvi.Activity.NewApp

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arvi.R
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.SnackBar


class SettingActivity : AppCompatActivity(), View.OnClickListener {


    var imgVwBackSA: ImageView? = null
    var spServerUrlSA: Spinner? = null
    var etServerUrlSA: EditText? = null

    var spScreensSA: Spinner? = null
    var spCameraFacingSA: Spinner? = null

    var rgGPSTrackSA: RadioGroup? = null
    var rbYesTrackSA: RadioButton? = null
    var rbNoTrackSA: RadioButton? = null

    var tvSaveSA: TextView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var strSelectedServerOption: String? = ""
    var strSelectedCameraFacing: String? = ""
    var strSelectedScreenOption: String? = ""
    var strSelectedGPSOption: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        try {
            setIDs()
            setListeners()
            strSelectedServerOption = SessionManager.getSelectedServerURL(context!!)
            strSelectedCameraFacing = SessionManager.getSelectedCameraFacing(context!!)
            strSelectedScreenOption = SessionManager.getSelectedDefaultScreen(context!!)
            strSelectedGPSOption = SessionManager.getSelectedGPSOption(context!!)

            if (!strSelectedServerOption.isNullOrEmpty()){
                etServerUrlSA!!.setText(strSelectedServerOption)
            }

            if (!strSelectedGPSOption.isNullOrEmpty()){
                if (strSelectedGPSOption.equals("Yes"))
                {
                    rbYesTrackSA!!.isSelected = true
                    rbNoTrackSA!!.isSelected = false
                }else{
                    rbYesTrackSA!!.isSelected = false
                    rbNoTrackSA!!.isSelected = true
                }
            }

            //setDefaultServerURLSpinnerData()
            setDefaultScreenSpinnerData()
            setDefaultCameraFacingSpinnerData()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectSpinnerValue(spinner: Spinner, myString: String) {
        try {
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i).toString() == myString) {
                    spinner.setSelection(i)
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun setListeners() {
        try {
            imgVwBackSA!!.setOnClickListener(this)
            tvSaveSA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setIDs() {
        try {
            context = SettingActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackSA = findViewById(R.id.imgVwBackSA)
            etServerUrlSA = findViewById(R.id.etServerUrlSA)

            spServerUrlSA = findViewById(R.id.spServerUrlSA)
            spScreensSA = findViewById(R.id.spScreensSA)
            spCameraFacingSA = findViewById(R.id.spCameraFacingSA)

            rgGPSTrackSA = findViewById(R.id.rgGPSTrackSA)
            rbYesTrackSA = findViewById(R.id.rbYesTrackSA)
            rbNoTrackSA = findViewById(R.id.rbNoTrackSA)

            tvSaveSA = findViewById(R.id.tvSaveSA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultCameraFacingSpinnerData() {
        try {
            val facingOption = resources.getStringArray(R.array.camera_facing_option)
            val adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, facingOption)
            spCameraFacingSA!!.adapter = adapter

            spCameraFacingSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    strSelectedCameraFacing = facingOption[position]
                    Log.e("SelectedCameraFacing", strSelectedCameraFacing)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            if (!strSelectedCameraFacing.isNullOrEmpty()) {
                selectSpinnerValue(spCameraFacingSA!!, strSelectedCameraFacing!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setDefaultScreenSpinnerData() {
        try {
            val screenOption = resources.getStringArray(R.array.default_screen_option)
            val adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, screenOption)
            spScreensSA!!.adapter = adapter

            spScreensSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    strSelectedScreenOption = screenOption[position]
                    Log.e("SelectedScreenOption", strSelectedScreenOption)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            if (!strSelectedScreenOption.isNullOrEmpty()) {
                selectSpinnerValue(spScreensSA!!, strSelectedScreenOption!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

/*
    private fun setDefaultServerURLSpinnerData() {
        try {
            val serverOption = resources.getStringArray(R.array.default_server_url)
            val adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, serverOption)
            spServerUrlSA!!.adapter = adapter

            spServerUrlSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    strSelectedServerOption = serverOption[position]
                    Log.e("SelectedServerOption", strSelectedServerOption)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/


    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        try {
            when (view!!.id) {
                R.id.imgVwBackSA -> {
                    finish()
                }
                R.id.tvSaveSA -> {
                    strSelectedServerOption = etServerUrlSA!!.text.toString()
                    if (strSelectedServerOption.isNullOrEmpty()) {
                        SnackBar.showValidationError(
                            context!!,
                            snackbarView!!,
                            "please enter your server url"
                        )
                        etServerUrlSA!!.requestFocus()
                    } else {
                        val selectedOption: Int = rgGPSTrackSA!!.checkedRadioButtonId
                        var radioButton = findViewById(selectedOption) as RadioButton
                        var selectedGpsOption = radioButton.text
                        strSelectedGPSOption = selectedGpsOption.toString()
                        Log.e("selectedGpsOption = ", strSelectedGPSOption)
                        Log.e("selectedScreen = ", strSelectedScreenOption)
                        Log.e("selectedFacing = ", strSelectedCameraFacing)
                        Log.e("selectedServer = ", strSelectedServerOption)
                        //todo:: set data in session
                        SessionManager.setSelectedServerURL(context!!, strSelectedServerOption!!)
                        SessionManager.setSelectedDefaultScreen(
                            context!!,
                            strSelectedScreenOption!!
                        )
                        SessionManager.setSelectedCameraFacing(context!!, strSelectedCameraFacing!!)
                        SessionManager.setSelectedGPSOption(context!!, strSelectedGPSOption!!)

                        var builder = AlertDialog.Builder(context!!)
                        builder.setCancelable(false)
                        builder.setTitle("Settings")
                        builder.setMessage("Setting saved successfully")
                        builder.setPositiveButton(
                            "Ok",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                finish()
                            })
                        var dialog = builder.create()
                        dialog.show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}