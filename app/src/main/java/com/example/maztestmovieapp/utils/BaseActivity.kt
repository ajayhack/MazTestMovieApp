package com.example.maztestmovieapp.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.maztestmovieapp.R

//region==================Base Activity to create Progress Dialog make other Activity use this functionality by extending this Activity:-
abstract class BaseActivity : AppCompatActivity(), IDialog {
    private lateinit var progressDialog: Dialog
    lateinit var progressTitleMsg: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setProgressDialog()
    }

    private fun setProgressDialog() {
        progressDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.item_progress_dialog)
            setCancelable(false)
        }
        progressTitleMsg = progressDialog.findViewById(R.id.msg_et)
    }

    override fun showProgress(progressMsg: String) {
        if (!progressDialog.isShowing && !(this as Activity).isFinishing) {
            progressTitleMsg.text = progressMsg
            progressDialog.show()
        }
    }


    override fun hideProgress() {
        if (progressDialog.isShowing && !(this as Activity).isFinishing)
            progressDialog.dismiss()
    }
}

interface IDialog {
    fun showProgress(progressMsg: String = "Please Wait....")
    fun hideProgress()
}
//endregion