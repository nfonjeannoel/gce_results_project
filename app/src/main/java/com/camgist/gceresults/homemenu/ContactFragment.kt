package com.camgist.gceresults.homemenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.camgist.gceresults.R
import com.camgist.gceresults.databinding.FragmentContactUsBinding

class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContactUs.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.trim().isNullOrEmpty()) {
                Toast.makeText(context, "please enter message", Toast.LENGTH_LONG).show()
            } else {
                val name = binding.etName.text.toString()
                val email = binding.etEmail.text.toString()
                val phone = binding.etPhone.text.toString()
//                var sendIntent = Intent(Intent.ACTION_MAIN)
//                sendIntent.apply {
//                    action = Intent.ACTION_VIEW
//                    `package` = "com.whatsapp"
//                    val url = "https://api.whatsapp.com/send?phone="+"237680787547"+"&text="+
//                            "Hi, i am contacting you with regards to the gce app results " +"\n \n" +
//                            message +"\n" + name +"\n" + email +"\n \n" + phone
//                    data = Uri.parse(url)
//                }
//                if (sendIntent.resolveActivity(requireContext().packageManager) != null){
//                    startActivity(sendIntent)
//                }else{
//                    Toast.makeText(context, "Please install whatsapp", Toast.LENGTH_LONG).show()
//                }

                val messageBody =
                    "Hi, i am contacting you with regards to the gce app results " + "\n \n" +
                            message + "\n" + name + "\n" + email + "\n \n" + phone

                sendEmail(messageBody)
            }

        }
    }

    private fun sendEmail(message: String) {
        val recipient = "nfonjeannoel1@gmail.com"
        val subject = "GCE RESULTS APP"
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        } catch (e: Exception) {
            Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show()
        }

    }

}