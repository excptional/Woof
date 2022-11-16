package com.example.woof.UserActivities.NormalUser.fragments

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


class HospitalMap : Fragment(), OnMapReadyCallback {

    companion object {
        private const val PHONE_CALL_CODE = 100
        private const val SEND_SMS_CODE = 101
    }

    private lateinit var hospitalName: TextView
    private lateinit var hospitalNumber: TextView
    private lateinit var hospitalAddress: TextView
    private lateinit var hospitalWebsite: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingText: TextView
    private lateinit var callBtn: ImageView
    private lateinit var smsBtn: ImageView
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var hospitalWebsiteLayout: LinearLayout
    private lateinit var hospitalNumberLayout: CardView
    private lateinit var name: String
    private lateinit var number: String
    private lateinit var address: String
    private lateinit var website: String
    private lateinit var ratings: String
    private var lat: Double = 0.0
    private var long: Double = 0.0


    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hospital_map, container, false)

        name = requireArguments().getString("name")!!
        address = requireArguments().getString("address")!!
        number = requireArguments().getString("number")!!
        website = requireArguments().getString("website")!!
        ratings = requireArguments().getString("ratings")!!

        hospitalName = view.findViewById(R.id.hospitalTitle)
        hospitalNumber = view.findViewById(R.id.hospitalNumber)
        hospitalAddress = view.findViewById(R.id.hospitalAddress)
        hospitalWebsite = view.findViewById(R.id.hospitalWebsite)
        ratingBar = view.findViewById(R.id.hospitalRatingBar)
        ratingText = view.findViewById(R.id.hospitalRatingText)
        callBtn = view.findViewById(R.id.hospitalCallBtn)
        smsBtn = view.findViewById(R.id.hospitalSMSBtn)
        mapView = view.findViewById(R.id.mapView)
        hospitalWebsiteLayout = view.findViewById(R.id.hospitalWebsiteLayout)
        hospitalNumberLayout = view.findViewById(R.id.hospitalNumberLayout)

        if (website.isEmpty()) hospitalWebsiteLayout.visibility = View.GONE

        if (number.isEmpty()) hospitalNumberLayout.visibility = View.GONE

        hospitalName.text = name
        hospitalAddress.text = address
        hospitalNumber.text = number
        hospitalWebsite.text = website
        ratingText.text = ratings
        ratingBar.rating = ratings.toFloat()

        val geocoder: Geocoder = Geocoder(requireContext())
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

        hospitalNumber.setOnClickListener {
            val myClipboard =
                getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", number)
            myClipboard.setPrimaryClip(clip)
            showToast("Number copied")
        }

        hospitalWebsite.setOnClickListener {
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