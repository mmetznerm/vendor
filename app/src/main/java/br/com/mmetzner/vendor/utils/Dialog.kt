package br.com.mmetzner.vendor.utils

import android.app.AlertDialog
import android.content.Context
import br.com.mmetzner.vendor.R

object Dialog {
    fun showError(context: Context, errorMessage: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(context.getString(R.string.error))
        dialog.setMessage(errorMessage)
        dialog.show()
    }
}