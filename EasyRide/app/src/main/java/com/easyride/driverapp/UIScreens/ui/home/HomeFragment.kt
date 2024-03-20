package com.easyride.driverapp.UIScreens.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.easyride.driverapp.MapSource.DownloadTask
import com.easyride.driverapp.MapSource.MapDownloadTaskInterface
import com.easyride.driverapp.R
import com.easyride.driverapp.databinding.FragmentHomeBinding
import com.easyride.driverapp.viewmodels.HomeFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface HomeFragmentInterface {
    fun onmenubarclicked()

}
class HomeFragment : Fragment(), OnMapReadyCallback, MapDownloadTaskInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var mMap: GoogleMap? = null
    var delegate: HomeFragmentInterface? = null
    var marker: Marker? = null
    private var viewmodel = HomeFragmentViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        homeViewModel.text.observe(viewLifecycleOwner) {
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        createRoute()
    }

    fun createRoute() {
        val sorucelocation = LatLng(35.420394334947304, -80.7455443501778)
        val destinatio = LatLng(35.309290611260636, -80.71666220148369)
        drawRoute(startPoint = sorucelocation, endPoint = destinatio)
    }

    private fun drawRoute(startPoint: LatLng, endPoint: LatLng) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val directionsResult = getDirections(startPoint, endPoint)

                if (directionsResult != null && directionsResult.routes.isNotEmpty()) {
                    val route = directionsResult.routes[0]

                    // Reverse the polyline points
                    val polylinePoints = route.overviewPolyline.decodePath()

                    // Add polyline
                    val polylineOptions = PolylineOptions()
                        .addAll(polylinePoints.map {
                            LatLng(it.lat, it.lng)
                        })
                        .width(10f)
                        .color(Color.BLACK) // Set your desired color here
                    mMap?.addPolyline(polylineOptions)

                    // Add marker at start point


                    val desiredWidth = 100
                    val desiredHeight = 100
                    val carBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.carmapimage)
                    val resizedBitmap = Bitmap.createScaledBitmap(carBitmap, desiredWidth, desiredHeight, true)
                    val carIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                    val startMarkerOptions = MarkerOptions()
                        .position(startPoint)
                        .icon(carIcon)
                        .title("")
                    mMap?.addMarker(startMarkerOptions)

                    // Set camera position at start point


                    // Zoom to fit the entire route
                    val builder = LatLngBounds.Builder()
                    for (point in polylinePoints) {
                        builder.include(LatLng(point.lat, point.lng))
                    }
                    val bounds = builder.build()
                    val padding = 100 // Padding in pixels
                    val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bounds, padding)
//                    mMap?.animateCamera(cameraUpdateBounds)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startPoint, 11f)
                    mMap?.moveCamera(cameraUpdate)


                } else {
                    // Handle case where no routes are found
                    Log.e("drawRoute", "No routes found")
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., network issues, API errors)
                Log.e("drawRoute", "Error drawing route: ${e.message}", e)
            }
        }
    }

    private suspend fun getDirections(startPoint: LatLng, endPoint: LatLng): DirectionsResult? {
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.google_maps_key))
            .build()

        return DirectionsApi.newRequest(context)
            .mode(TravelMode.DRIVING)
            .origin(com.google.maps.model.LatLng(startPoint.latitude, startPoint.longitude))
            .destination(com.google.maps.model.LatLng(endPoint.latitude, endPoint.longitude))
            .await()
    }

    override fun getMapPolygon(options: PolylineOptions?) {
        mMap?.let { map ->
            options?.let { polylineOptions ->
                map.addPolyline(polylineOptions)
            }
        }
    }

}