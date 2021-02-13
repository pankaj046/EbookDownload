package sharma.pankaj.itebooks.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import sharma.pankaj.itebooks.R


fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG ).show()
}

fun View.dialog(context: Context) : Dialog{
    val dialog = Dialog(context)
    val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
    dialog.setContentView(inflate)
    dialog.window?.setLayout(200, 200);
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
    )
    return dialog
}

fun View.snackBar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}
