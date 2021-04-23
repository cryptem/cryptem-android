package io.cryptem.app.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentMapBinding
import io.cryptem.app.ui.base.BaseFragment


@AndroidEntryPoint
class MapFragment : BaseFragment<MapVM, FragmentMapBinding>(R.layout.fragment_map) {
    override val viewModel: MapVM by viewModels()

    companion object {
        const val REQUEST_LOCATION = 1
    }

    private var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        initMap()
        viewModel.location.observe(viewLifecycleOwner){
            val previousZoomLevel = 13.00f
            val zoomPoint = LatLng(it.latitude, it.longitude)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomPoint, previousZoomLevel))
        }
    }

    private fun checkLocationPermission() {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_LOCATION
            )
        } else {
            onLocationEnabled()
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocationEnabled(){
        viewModel.getLastLocation()
        map?.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION && grantResults.first() == PackageManager.PERMISSION_GRANTED){
           onLocationEnabled()
        }
    }

    private fun initMap() {
        binding.mapView.getMapAsync { m ->
            map = m
            checkLocationPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        super.onDestroyView()
    }
}