package com.example.woof.UserActivities.fragments

import android.Manifest.permission.CALL_PHONE
import android.Manifest.permission.SEND_SMS
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.woof.R
import com.example.woof.R.color.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapShow : Fragment(), OnMapReadyCallback {

    companion object {
        private const val PHONE_CALL_CODE = 100
        private const val SEND_SMS_CODE = 101
    }

    private lateinit var objectName: TextView
    private lateinit var objectNumber: TextView
    private lateinit var objectAddress: TextView
    private lateinit var objectWebsite: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingText: TextView
    private lateinit var callBtn: ImageView
    private lateinit var smsBtn: ImageView
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var objectWebsiteLayout: LinearLayout
    private lateinit var objectNumberLayout: CardView
    private lateinit var name: String
    private lateinit var number: String
    private lateinit var address: String
    private lateinit var website: String
    private lateinit var ratings: String
    private var lat: Double = 0.0
    private var long: Double = 0.0


    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_show, container, false)

        name = requireArguments().getString("name")!!
        address = requireArguments().getString("address")!!
        number = requireArguments().getString("number")!!
        website = requireArguments().getString("website")!!
        ratings = requireArguments().getString("ratings")!!

        objectName = view.findViewById(R.id.objectTitle)
        objectNumber = view.findViewById(R.id.objectNumber)
        objectAddress = view.findViewById(R.id.objectAddress)
        objectWebsite = view.findViewById(R.id.objectWebsite)
        ratingBar = view.findViewById(R.id.objectRatingBar)
        ratingText = view.findViewById(R.id.objectRatingText)
        callBtn = view.findViewById(R.id.objectCallBtn)
        smsBtn = view.findViewById(R.id.objectSMSBtn)
        mapView = view.findViewById(R.id.mapView)
        objectWebsiteLayout = view.findViewById(R.id.objectWebsiteLayout)
        objectNumberLayout = view.findViewById(R.id.objectNumberLayout)

        if (website.isEmpty()) objectWebsiteLayout.visibility = View.GONE

        if (number.isEmpty()) objectNumberLayout.visibility = View.GONE

        objectName.text = name
        objectAddress.text = address
        objectNumber.text = number
        objectWebsite.text = website
        ratingText.text = ratings
        ratingBar.rating = ratings.toFloat()

        val geocoder = Geocoder(requireContext())
        var addressList = mutableListOf<Address>()

        try{
            addressList = geocoder.getFromLocationName(address, 1)!!

            lat = addressList[0].latitude
            long = addressList[0].longitude

        }catch (e: Exception){
            val msg = getErrorMassage(e)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        callBtn.setOnClickListener {
            checkPermission(
                CALL_PHONE,
                PHONE_CALL_CODE
            )
        }

        smsBtn.setOnClickListener {
            checkPermission(
                SEND_SMS,
                SEND_SMS_CODE
            )
        }

        objectNumber.setOnClickListener {
            val myClipboard =
                getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", number)
            myClipboard.setPrimaryClip(clip)
            showToast("Number copied")
        }

        objectWebsite.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(
                ContextCompat.getColor(
                    requireContext(),
                    cream
                )
            )
            builder.addDefaultShareMenuItem()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(website))
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it
        }
        val location = LatLng(lat, long)
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Marker")
        )
        val pos = LatLng(lat, long)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f))
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        } else {
            if (requestCode == SEND_SMS_CODE) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null))
                intent.putExtra("sms_body", "Hello, ")
                startActivity(intent)
            } else {
                val dialIntent = Intent(Intent.ACTION_CALL)
                dialIntent.data = Uri.parse("tel:$number")
                startActivity(dialIntent)
            }
        }
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Phone Call Permission Granted")
                val dialIntent = Intent(Intent.ACTION_CALL)
                dialIntent.data = Uri.parse("tel:$number")
                startActivity(dialIntent)
            } else {
                showToast("Phone Call Permission Denied")
            }
        } else if (requestCode == SEND_SMS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Send SMS Permission Granted")
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null))
                intent.putExtra("sms_body", "Hello, ")
                startActivity(intent)
            } else {
                showToast("Send SMS Permission Denied")
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}