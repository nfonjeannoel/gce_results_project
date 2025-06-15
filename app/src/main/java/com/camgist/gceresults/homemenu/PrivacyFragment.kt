package com.camgist.gceresults.homemenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.camgist.gceresults.R

class PrivacyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Handle privacy policy text links
        val privacy: TextView? = view.findViewById(R.id.privacy_policy)
        privacy?.movementMethod = LinkMovementMethod.getInstance()
        
        // Handle full policy button click
        val btnViewFullPolicy: Button? = view.findViewById(R.id.btn_view_full_policy)
        btnViewFullPolicy?.setOnClickListener {
            openUrl("https://gceresults.jeangineer.com/privacy")
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error if no browser is available
        }
    }
}