package com.camgist.gceresults.resultlist.details

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.camgist.gceresults.R
import com.camgist.gceresults.databinding.FragmentDetailsBinding
import com.camgist.gceresults.resultlist.ResultListFragment
import com.camgist.gceresults.resultlist.SharedViewModel
import com.camgist.gceresults.resultlist.details.adapters.StudentRecordAdapter
import com.camgist.gceresults.resultlist.details.algorithms.Records
import com.camgist.gceresults.resultlist.details.algorithms.getRecords
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class DetailsFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var binding: FragmentDetailsBinding
//    private lateinit var viewModel: DetailsVewModel
//    private lateinit var viewModelFactory: DetailsViewModelFactory

    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity().supportFragmentManager.findFragmentByTag(ResultListFragment.TAG) ?: this).get(SharedViewModel::class.java)
    }

    private lateinit var args: DetailsFragmentArgs
    private lateinit var studentRecord: List<Records>
    private lateinit var adapter: StudentRecordAdapter
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        args = DetailsFragmentArgs.fromBundle(requireArguments())


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"ca-app-pub-5796541899764838/1649575743", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
//                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                Log.d(TAG, 'Ad was loaded.')
                mInterstitialAd = interstitialAd
            }
        })

        sharedViewModel.setStudentResult(args.studentResult)

//        viewModelFactory = DetailsViewModelFactory(args.studentResult)
//        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailsVewModel::class.java)

        studentRecord =
            getRecords(args.studentResult.student_grades ?: "", args.studentResult.level,
                args.studentResult.year).toList()

        adapter = StudentRecordAdapter(studentRecord)
        binding.rvSubjects.adapter = adapter

        sharedViewModel.result.observe(viewLifecycleOwner, Observer {
            binding.apply {
                (activity as AppCompatActivity).supportActionBar?.title = it.student_name
                tvStudentName.text = it.student_name
                tvSchool.text = it.center_name
                tvCenterNumber.text = getString(R.string.center_number, it.center_number)
                tvYearLevel.text = "${it.level}-${it.year}"
                if (it.year >= 2023){
                    tvPoints.text = getString(R.string.total_points, "N/A")
                    if (it.level.endsWith("t", true) ){
                        tvPapersPassed.text = getString(R.string.papers_passed, "N/A")

                    } else{
                        tvPapersPassed.text = getString(R.string.papers_passed, it.papers_passed ?: "N/A")

                    }

                } else{
                    tvPoints.text = getString(R.string.total_points, calculateTotalPoint().toString())
                    tvPapersPassed.text = getString(R.string.papers_passed, it.papers_passed ?: "N/A")

                }
            }
        })

    }

    private fun getLevel(level: String): String {
        return when(level){
            "ALG" -> "A-Level General"
            "ALT" -> "A-Level Technical"
            "OLG" -> "O-Level General"
            "OLT" -> "O-Level Technical"
            else -> level
        }
    }

    private fun calculateTotalPoint(): Int {
        var total = 0
        for (record in studentRecord) {
            total += record.point
        }
        return total
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_home -> {
                findNavController().navigate(
                    DetailsFragmentDirections.actionDetailsFragmentToHomeFragment()
                )
            }
            R.id.mi_share -> {
                shareResultText()
            }
            R.id.mi_download -> {
//                Toast.makeText(context, "downloaded", Toast.LENGTH_LONG).show()
                getScreenshot()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareResultText() {
        val record = args.studentResult
        val resultText = StringBuilder()
        resultText.apply {
            append(record.student_name)
            appendLine()
            append("passed in ${record.papers_passed ?: "N/A"} papers")
            appendLine()
            for (paper in studentRecord){
                append("${paper.subjectName}: ${paper.grade}")
                appendLine()
            }
            append("total point: ${calculateTotalPoint()}")
        }

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, resultText.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun getScreenshot() {
        // For Android 10 (API 29) and above, we don't need WRITE_EXTERNAL_STORAGE permission
        // when using MediaStore API with scoped storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No permissions needed for scoped storage
            captureAndSaveScreenshot()
        } else {
            // For Android 9 and below, we still need WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                captureAndSaveScreenshot()
            } else {
                requestSharePermission()
            }
        }
    }
    
    private fun captureAndSaveScreenshot() {
        try {
            binding.screenshotLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_white))
            val bitmap = getScreenShotFromView(binding.screenshotLayout)
            binding.screenshotLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.alpha_bg10)
            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            } else {
                Toast.makeText(context, "Failed to capture screenshot", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("DetailsFragment", "Error capturing screenshot", e)
            Toast.makeText(context, "Failed to capture screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSharePermission() {
        // Only request permission for Android 9 and below
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return screenshot
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(requireActivity())
        } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

        // Generating a file name
        val filename = "my result${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            context?.contentResolver?.also { resolver ->
                // Content resolver will process the content values
                val contentValues = ContentValues().apply {
                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }

                requestToShare(imageUri)

            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
            Toast.makeText(context, "Result saved to Gallery", Toast.LENGTH_SHORT).show()
        }
        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

    }

    private fun requestToShare(imageUri: Uri?) {
        val snack = Snackbar.make(requireView(),"Result saved to Gallery", 10000)
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.blue_navbar))
        snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue_white))
        snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_light))

        snack.setAction("Share", View.OnClickListener {
            // executed when DISMISS is clicked
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, "Send to"))
        })
        snack.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to capture screenshot again
                captureAndSaveScreenshot()
            } else {
                // Permission denied
                Toast.makeText(
                    context, 
                    "Storage permission is required to save the screenshot", 
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}