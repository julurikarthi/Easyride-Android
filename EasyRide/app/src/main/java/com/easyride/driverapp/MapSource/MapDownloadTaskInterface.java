package com.easyride.driverapp.MapSource;

import com.google.android.gms.maps.model.PolylineOptions;

public interface MapDownloadTaskInterface {
    void getMapPolygon(PolylineOptions options);
}
