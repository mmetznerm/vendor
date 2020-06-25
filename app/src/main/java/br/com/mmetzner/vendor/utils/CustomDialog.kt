package br.com.mmetzner.vendor.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Window
import br.com.mmetzner.vendor.R

object CustomDialog {

    private var loadingDialog: Dialog? = null

    fun showError(context: Context, errorMessage: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(context.getString(R.string.error))
        dialog.setMessage(errorMessage)
        dialog.show()
    }

    fun loadingDialog(context: Context, show: Boolean) {
        if(show) {
            loadingDialog = Dialog(context)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.setContentView(R.layout.loading_component)
            loadingDialog?.setCancelable(false)
            loadingDialog?.show()
        } else {
            loadingDialog?.hide()
        }

    }
}