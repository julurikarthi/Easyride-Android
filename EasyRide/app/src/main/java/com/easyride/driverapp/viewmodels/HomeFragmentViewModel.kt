package com.easyride.driverapp.viewmodels

class HomeFragmentViewModel {

    fun getDirectionsUrl(origin: com.google.android.gms.maps.model.LatLng, dest: com.google.android.gms.maps.model.LatLng): String? {

        // Origin of route
        val str_origin =
            "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest =
            "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val key = "key=AIzaSyCIVEtSk-0GZBgeVVezSYSnKMmuxb6umSM"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=AIzaSyCIVEtSk-0GZBgeVVezSYSnKMmuxb6umSM"
    }
}