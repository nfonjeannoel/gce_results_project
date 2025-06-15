package com.camgist.gceresults

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.camgist.gceresults.network.MetadataService
import com.google.android.material.bottomsheet.BottomSheetDialog

fun selectLevel(level: String): String {
    return when(level){
        "A-Level General" -> "ALG"
        "A-Level Technical" -> "ALT"
        "O-Level General" -> "OLG"
        "O-Level Technical" -> "OLT"
        else -> ""
    }
}

/**
 * New function to select level using metadata service
 * This should be used instead of selectLevel when context is available
 */
fun selectLevelWithMetadata(context: Context, level: String): String {
    val metadataService = MetadataService(context)
    return metadataService.getLevelCodeFromDisplayName(level)
}

fun showEmptyFieldToast(context: Context){
    Toast.makeText(context, "One or more fields are empty", Toast.LENGTH_LONG).show()
}

fun showNoMatchFragment(
    layoutInflater: LayoutInflater,
    requireContext: Context,
    noMatchDialog: Int
) {
    val dialog = BottomSheetDialog(requireContext)
    val view = layoutInflater.inflate(R.layout.no_match_dialog, null)
    val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
    btnClose.setOnClickListener {
        dialog.dismiss()
    }
    dialog.setCancelable(true)
    dialog.setContentView(view)
    dialog.show()
}

fun showNoInternetFragment(
    layoutInflater: LayoutInflater,
    requireContext: Context,
    noInternetDialog: Int
) {
    val dialog = BottomSheetDialog(requireContext)
    val view = layoutInflater.inflate(R.layout.no_internet_dialog, null)
    val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
    btnClose.setOnClickListener {
        dialog.dismiss()
    }
    dialog.setCancelable(true)
    dialog.setContentView(view)
    dialog.show()
}

fun showErrorFragment(layoutInflater: LayoutInflater, requireContext: Context, layout : Int) {
    val dialog = BottomSheetDialog(requireContext)
    val view = layoutInflater.inflate(layout, null)
    val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
    btnClose.setOnClickListener {
        dialog.dismiss()
    }
    dialog.setCancelable(true)
    dialog.setContentView(view)
    dialog.show()
}