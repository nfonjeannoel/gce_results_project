package com.camgist.gceresults.home.tabs

import android.content.Context
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.camgist.gceresults.R
import com.google.android.material.button.MaterialButton

 fun enableButton(myButton: Button, ivLoading: ImageView, llAllResults: LinearLayout) {
    (myButton as MaterialButton).apply {
        text = "Fetch Result"
        icon = ContextCompat.getDrawable(context, R.drawable.ic_search_white)
        isEnabled = true
    }
    ivLoading.visibility = View.GONE
    llAllResults.alpha = 1.0f
}

fun showToast(message: String, context: Context) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

fun removeHelp(tvHelp: TextView) {
    if (tvHelp.isVisible){
        tvHelp.visibility = View.GONE
    } else{
        tvHelp.visibility = View.VISIBLE
    }
}

 fun disableButton(
     it: View?,
     requireContext: Context,
     ivLoading: ImageView,
     llMyResult: LinearLayout
 ) {


    (it as MaterialButton).apply {
        text = "Please wait..."
        icon = ContextCompat.getDrawable(requireContext, R.drawable.loading_img)
        isEnabled = false
    }
     ivLoading.visibility = View.VISIBLE
     llMyResult.alpha = 0.7f
}