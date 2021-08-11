package io.cryptem.app.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.FragmentMapBinding
import io.cryptem.app.model.ui.Poi
import io.cryptem.app.model.ui.PoiCategory
import io.cryptem.app.ui.base.BaseFragment
import io.cryptem.app.ui.base.event.UrlEvent
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class MapFragment : BaseFragment<MapVM, FragmentMapBinding>(R.layout.fragment_map) {
    override val viewModel: MapVM by viewModels()

    companion object {
        const val REQUEST_LOCATION = 1
        const val ZOOM = 13f
    }

    private var map: GoogleMap? = null
    private val markersMap = HashMap<Marker, Poi>()
    private val markers = ArrayList<Marker>()
    private val markerIcons = HashMap<String, BitmapDescriptor?>()
    private var defaultMarker: BitmapDescriptor? = null
    var countryMenuItem : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        defaultMarker = createMarkerBitmap(R.drawable.ic_poi_other)

        initMap()
        viewModel.location.observe(viewLifecycleOwner) {
            it?.let {
                val zoomPoint = LatLng(it.latitude, it.longitude)
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomPoint, ZOOM))
            }
        }
        viewModel.pois.observe(viewLifecycleOwner) {
            addMarkers(it)
        }
        observe(UrlEvent::class){
            showUrl(it.url)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (viewModel.selectedPoi.value != null){
                viewModel.selectedPoi.value = null
                return@addCallback
            } else {
                navController().navigateUp()
            }
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
    private fun onLocationEnabled() {
        viewModel.getLastLocation()
        map?.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            onLocationEnabled()
        }
    }

    private fun initMap() {
        binding.mapView.getMapAsync { m ->
            map = m
            checkLocationPermission()
            if (viewModel.isCountrySupported()){
                viewModel.loadData()
            } else {
                showUnsupportedCountryDialog()
            }
            map?.setOnMarkerClickListener { marker ->
                return@setOnMarkerClickListener markersMap[marker]?.let {
                    viewModel.selectedPoi.value = it
                    true
                } ?: false
            }
        }
    }

    fun showUnsupportedCountryDialog(){
        MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.map_unsupported_country_title)
            .setMessage(
                getString(
                    R.string.map_unsupported_country_message, Locale(
                        Locale.getDefault().language,
                        viewModel.getCountry()
                    ).displayCountry
                )
            )
            .setPositiveButton(R.string.ok){ _, _ -> }
            .setNegativeButton(R.string.donate){ _, _ -> navigate(R.id.fragmentAbout)}
            .show()
    }

    fun addMarkers(pois: List<Poi>) {
        removeMarkers()
        pois.forEach { poi ->
            val options = MarkerOptions().apply {
                position(
                    LatLng(
                        poi.latitude,
                        poi.longitude
                    )
                )
                title(poi.name)
                icon(getMarkerIcon(poi.category) ?: defaultMarker)
            }
            map?.addMarker(options)?.let { marker ->
                markers.add(marker)
                markersMap[marker] = poi
            }
        }
    }

    private fun getMarkerIcon(category: PoiCategory?) : BitmapDescriptor? {
        return category?.let {
            if (!markerIcons.contains(category.id)){
                markerIcons[category.id] = createMarkerBitmap(category.getIcon())
            }
            return markerIcons[category.id] ?: defaultMarker
        }
    }

    private fun createMarkerBitmap(vectorResId: Int): BitmapDescriptor? {
        val size = 128
        val sidePadding = 40
        val topOffset = 20

        return ContextCompat.getDrawable(requireContext(), vectorResId)?.run {
            val background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker)
            background!!.setBounds(0, 0, size, size)
            setBounds(sidePadding, sidePadding-topOffset, size - sidePadding, size - sidePadding - topOffset)
            val bitmap = Bitmap.createBitmap(
                size,
                size,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            background.draw(canvas)
            this.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun removeMarkers() {
        markers.forEach {
            it.remove()
        }
        markers.clear()
        markersMap.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map, menu)
        countryMenuItem = menu.findItem(R.id.action_country)
        val subMenu = countryMenuItem?.subMenu
        viewModel.countries.forEach {
            subMenu?.add(it)
        }
        countryMenuItem?.title = viewModel.country.value
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            navigate(R.id.action_mapFragment_to_poiEditorFragment)
        }
        if (item.itemId == 0){
            viewModel.country.value = item.title.toString()
            countryMenuItem?.title = item.title
        }
        return super.onOptionsItemSelected(item)
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